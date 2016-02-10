#! /usr/bin/env python
# -*- coding: utf-8 -*-

import markdown
from markdown.treeprocessors import Treeprocessor
from markdown.blockprocessors import BlockProcessor
import re
from markdown import util
import xml.etree.ElementTree as ET
import copy
from markdown.inlinepatterns import IMAGE_LINK_RE

class InFigureParser(object):
    
    def transform(self,  parent, element, legend, index, InP = False):
        if InP:
            lelems = list(element.iter())
            oldImg = lelems[-1]
            element.remove(oldImg)
        else:
            oldImg = element

        nFig = util.etree.Element("figure")
        nFigCaption = util.etree.Element("figcaption")
        contentLegend = legend.items()
        for el in legend:
            legend.remove(el)
            nFigCaption.append(el)
        
        nFig.append(oldImg)
        nFig.append(nFigCaption)
        parent.remove(element)
        parent.remove(legend)
        parent.insert(index, nFig)


class FigureParser(InFigureParser):
    def __init__(self, ignoringImg):
        InFigureParser.__init__(self)
        self.ignoringImg = ignoringImg
        self.ree = re.compile(r"^" + IMAGE_LINK_RE + r"(\n|$)")

    def detect(self, element, type):
        if element == None:
            return False
        lelems = list(element.iter())
        #print repr(element.text)
        return  (type == "unknown" or type == "Figure") \
                and element.tag=="p" \
                and( ( element.text is not None \
                and self.ree.search(element.text)) \
                or ( (element.text is None or element.text.strip() == "") \
                and (len(lelems) == 1 or (len(lelems)==2 and lelems[0] is element)) \
                and lelems[-1].tag == "img" \
                and (lelems[-1].attrib["src"] not in self.ignoringImg)))
            
    def transform(self,  parent, element, legend, index):
        InFigureParser.transform(self, parent, element, legend, index, True)

class EquationParser(InFigureParser):

    def detect(self, element, type):
        if element == None:
            return False
        lelems = list(element.iter())
        return  (type == "unknown" or type == "Equation") \
                and element.tag=="p" \
                and (element.text is None or element.text.strip() == "") \
                and (len(lelems) == 1 or (len(lelems)==2 and lelems[0] is element)) \
                and lelems[-1].tag == "mathjax"
    def transform(self,  parent, element, legend, index):
        InFigureParser.transform(self, parent, element, legend, index, True)


class CodeParser(InFigureParser):
    def __init__(self, md):
        self.md = md

    def detect(self, element, type):
        if element == None:
            return False
        if  (type == "unknown" or type == "Code") and element.tag=="p" :
            hs = self.md.htmlStash
            for i in range(hs.html_counter):
                if element.text == hs.get_placeholder(i) :
                    Teste = ET.fromstring(hs.rawHtmlBlocks[i][0].encode('utf-8'))
                    if Teste is not None and Teste.tag=="table" and "class" in Teste.attrib and Teste.attrib["class"] == "codehilitetable":
                        return True
                    else:
                        return False
        return False

class QuoteParser(InFigureParser):
    def detect(self, element, type):
        if element == None:
            return False
            
        return  (type == "unknown" or type == "Source") and element.tag=="blockquote"


class TableParser(object):
    def detect(self, element, type):
        if element == None:
            return False
        return  (type == "unknown" or type == "Table") and element.tag=="table"

    def transform(self,  parent, element, legend, index):
        parent.remove(legend)
        cap = util.etree.Element('caption')
        contentLegend = legend.items()
        for el in legend:
            legend.remove(el)
            cap.append(el)
        element.insert(0, cap)

class VideoParser(InFigureParser):
    def detect(self, element, type):
        if element == None:
            return False
        lelems = list(element.iter())
        return  (type == "unknown" or type == "Video") \
                and element.tag=="iframe" 


class SmartLegendProcessor(Treeprocessor):
    def __init__(self, parser, configs, md):
        Treeprocessor.__init__(self, parser)

        self.configs = configs

        self.processors = ( FigureParser(configs["IGNORING_IMG"]),
                            EquationParser(),
                            CodeParser(md),
                            TableParser(),
                            VideoParser(),
                            QuoteParser())
    
    def run(self, root):
        root = self.parse_legend(root)
        root = self.parse_autoimg(root)
        return root
    

    def parse_legend(self, root):
        elemsToInspect = [root]
        while len(elemsToInspect) > 0:
            elem = elemsToInspect.pop()
            Restart=True
            while Restart:
                Restart = False
                precedent = None
                i=0
                for nelem in elem:
                    if nelem.tag in self.configs["PARENTS"] and nelem not in elemsToInspect:
                        elemsToInspect.append(nelem)
                    if nelem.tag == "customlegend" and precedent is not None : # and len(list(nelem.itertext())) == 0 :
                        proc = self.detectElement(precedent, nelem.attrib["type"])
                        if proc is not None:
                            proc.transform(elem, precedent, nelem, i-1)
                            Restart = True
                            break
                    precedent = nelem
                    i+=1

        return root
    
    
    def parse_autoimg(self, root):
        elemsToInspect = [root]
        while len(elemsToInspect) > 0:
            elem = elemsToInspect.pop()
            Restart=True
            while Restart:
                Restart = False
                i=0
                for nelem in elem:
                    if nelem.tag in self.configs["PARENTS"] and nelem not in elemsToInspect:
                        elemsToInspect.append(nelem)
                    #Auto Legend for image
                    if nelem.tag == 'p'  and len(list(nelem.itertext())) == 0 :
                        lelems = list(nelem.iter())
                        if     (len(lelems) == 1 or (len(lelems)==2 and lelems[0] is nelem)) \
                                 and lelems[-1].tag == "img" \
                                 and lelems[-1].attrib["alt"] != "" \
                                 and not (lelems[-1].attrib["src"] in self.configs["IGNORING_IMG"]):
                            oldImg = lelems[-1]
                            nelem.remove(oldImg)
                            nFig = util.etree.Element("figure")
                            nFigCaption = util.etree.Element("figcaption")
                            nFigCaption.text = oldImg.attrib["alt"]
                            oldImg.attrib["alt"]=""
                            nFig.append(oldImg)
                            nFig.append(nFigCaption)
                            nelem.insert(i-1, nFig)
                            Restart = True
                            break
                    i+=1
        return root
    
    def detectElement(self, elem, legend):
        for proc in self.processors:
            if proc.detect(elem, legend) :
                return proc
        return None


class LegendProcessor(BlockProcessor):
    def __init__(self, parser, md, configs):
        BlockProcessor.__init__(self, parser)
        self.md = md
        self.configs = configs

        self.processors = ( FigureParser(configs["IGNORING_IMG"]),
                            EquationParser(),
                            CodeParser(md),
                            TableParser(),
                            VideoParser(),
                            QuoteParser())
        self.RE = re.compile(ur'(^|(?<=\n))((?P<typelegend>Figure|Table|Code|Equation|Video|Source)\s?)*\:\s?(?P<txtlegend>.*?)(\n|$)')
    
    def detectElement(self, elem, legend):
        for proc in self.processors:
            if proc.detect(elem, legend) :
                return proc
        return None

    def test(self, parent, block):
        
        mLeg = self.RE.search(block)
        if not bool(mLeg):
            return False
        else:
            return True
            
    def test_complete(self, parent, block):
        mLeg = self.RE.search(block)
        gd = mLeg.groupdict()
        if gd["typelegend"] is None:
            type = "unknown"
        else:
            type = gd["typelegend"]
        sibling = self.lastChild(parent)
        return self.detectElement(sibling, type) is not None

    def run(self, parent, blocks):
        
        block = blocks.pop(0)
        mLeg = self.RE.search(block)
        before = block[:mLeg.start()]
        after = block[mLeg.end():]
        contentStart = block[mLeg.start():mLeg.start("txtlegend")]
        cpp = None
        if before:
            cpp = copy.copy(parent)
            self.parser.parseBlocks(cpp, [before])
        else:
            cpp = parent
        if not self.test_complete(cpp, block):
            blocks.insert(0, block)
            return False
        elif before:
            self.parser.parseBlocks(parent, [before])
        nLegend = util.etree.Element("customlegend")
        self.parser.parseChunk(nLegend, mLeg.group('txtlegend'))
        gd = mLeg.groupdict()
        if gd["typelegend"] is None:
            nLegend.set("type", "unknown")
        else:
            nLegend.set("type", gd["typelegend"])
        nLegend.set("rawStart", contentStart)
        parent.append(nLegend)
        if after:
            blocks.insert(0,after)

class SmartLegendExtension(markdown.extensions.Extension):
    def __init__(self, configs={}):
        self.configs = {
            "IGNORING_IMG" : [],
            "PARENTS"      : [],
            }
        for key, value in configs.iteritems():
            self.configs[key] = value
        if "div" not in self.configs["PARENTS"]:
            self.configs["PARENTS"].append("div")
        pass

    def extendMarkdown(self, md, md_globals):
        md.registerExtension(self)
        md.treeprocessors.add('smart-legend', SmartLegendProcessor(md.parser,self.configs, md),"_end")
        md.parser.blockprocessors.add('legend-processor', LegendProcessor(md.parser,md, self.configs),"_begin")

def makeExtension(configs={}):
    return SmartImgExtension(configs=configs)
