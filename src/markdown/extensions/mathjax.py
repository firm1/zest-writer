# From https://github.com/mayoff/python-markdown-mathjax

import markdown

class MathJaxPattern(markdown.inlinepatterns.Pattern):

    def __init__(self):
        markdown.inlinepatterns.Pattern.__init__(self, r'(?<!\\)(?P<St>\$\$?)(.+?)(?<!\\)(?P=St)')

    def handleMatch(self, m):
        if m.group(2) == "$" and "\n" not in m.group(3):
            node = markdown.util.etree.Element('span')
            node.text = markdown.util.AtomicString("$" + m.group(3) + "$")
            return node
        else:
            dnode = markdown.util.etree.Element('div')
            dnode.set('class', "mathjax-wrapper")
            node = markdown.util.etree.SubElement(dnode, "mathjax")
            node.text = markdown.util.AtomicString("$$" + m.group(3) + "$$")
            return dnode

class MathJaxExtension(markdown.Extension):
    def extendMarkdown(self, md, md_globals):
        # Needs to come before escape matching because \ is pretty important in LaTeX
        md.inlinePatterns.add('mathjax', MathJaxPattern(), '<escape')

def makeExtension(configs=None):
    return MathJaxExtension(configs)
