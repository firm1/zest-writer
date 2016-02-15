"""
Python-Markdown Zds Extension
=============================

A compilation of various Python-Markdown extensions suitable for zds.

Note that each of the individual extensions still need to be available
on your PYTHONPATH. This extension simply wraps them all up as a
convenience so that only one extension needs to be listed when
initiating Markdown. See the documentation for each individual
extension for specifics about that extension.

In the event that one or more of the supported extensions are not
available for import, Markdown will issue a warning and simply continue
without that extension.

There may be additional extensions that are distributed with
Python-Markdown that are not included here in Extra. Those extensions
are not part of PHP Markdown Extra, and therefore, not part of
Python-Markdown Extra. If you really would like Extra to include
additional extensions, we suggest creating your own clone of Extra
under a differant name. You could also edit the `extensions` global
variable defined below, but be aware that such changes may be lost
when you upgrade to any future version of Python-Markdown.

"""

from __future__ import absolute_import
from __future__ import unicode_literals
from . import Extension

from .subsuperscript import SubSuperscriptExtension
from .delext import DelExtension
from .urlize import UrlizeExtension
from .kbd import KbdExtension
from .mathjax import MathJaxExtension
from .customblock import CustomBlockExtension
from .align import AlignExtension
from .video import VideoExtension
from .preprocessblock import PreprocessBlockExtension
from .emoticons import EmoticonExtension
from .grid_tables import GridTableExtension
from .comments import CommentsExtension
from .smartLegend import SmartLegendExtension
from .headerDec import DownHeaderExtension
from .smarty import SmartyExtension
from .codehilite import CodeHiliteExtension

class ZdsExtension(Extension):
    """ Add various extensions to Markdown class."""
    def __init__(self, *args, **kwargs):
        self.config = {
            'inline': [False, ''],
            'emoticons': [{}, ''],
            'js_support': [False, ''],
        }

        super(ZdsExtension, self).__init__(*args, **kwargs)

    def extendMarkdown(self, md, md_globals):
        """ Register extension instances. """
        self.inline = self.getConfigs().get("inline", True)
        self.emoticons = self.getConfigs().get("emoticons", {})
        self.js_support = self.getConfigs().get("js_support", False)

        # create extensions :
        sub_ext         = SubSuperscriptExtension() # Sub and Superscript support
        del_ext         = DelExtension()            # Del support
        urlize_ext      = UrlizeExtension()         # Autolink support
        sm_ext          = SmartyExtension(smart_quotes=False)
        if not self.inline:
            mathjax_ext     = MathJaxExtension()        # MathJax support
            kbd_ext         = KbdExtension()            # Keyboard support
            emo_ext         = EmoticonExtension({"EMOTICONS" : self.emoticons}) # smileys support
            customblock_ext = CustomBlockExtension(
                { "s(ecret)?"       : "spoiler",
                  "i(nformation)?"  : "information ico-after",
                  "q(uestion)?"     : "question ico-after",
                  "a(ttention)?"    : "warning ico-after",
                  "e(rreur)?"       : "error ico-after",
                })                                      # CustomBlock support
            align_ext       = AlignExtension()          # Right align and center support
            video_ext       = VideoExtension(js_support=self.js_support)          # Video support

            preprocess_ext  = PreprocessBlockExtension({"preprocess" : ("fenced_code_block", "footnote", "reference","abbr", )}) # Preprocess extension
            gridtable_ext   = GridTableExtension()      # Grid Table support
            comment_ext     = CommentsExtension({"START_TAG" : "<--COMMENT", "END_TAG" : "COMMENT-->"}) # Comment support
            legend_ext      = SmartLegendExtension({"IGNORING_IMG" : self.emoticons.values(), "PARENTS" : ("div", "blockquote")})       # Smart Legend support
            dheader_ext     = DownHeaderExtension({"OFFSET" : 2, }) # Offset header support
        # Define used ext
        exts = [sub_ext,                            # Subscript support
                del_ext,                            # Del support
                urlize_ext,                         # Autolink support
                sm_ext,
                ]
        if not self.inline:
            exts.extend([
                'markdown.extensions.abbr',                             # Abbreviation support, included in python-markdown
                'markdown.extensions.footnotes',                        # Footnotes support, included in python-markdown
                                                    # Footnotes place marker can be set with the PLACE_MARKER option
                'markdown.extensions.tables',                           # Tables support, included in python-markdown
                'markdown.extensions.fenced_code',                      # Extended syntaxe for code block support, included in python-markdown
                CodeHiliteExtension(linenums=True,guess_lang=False),
                                                    # Code hightlight support, with line numbers, included in python-markdwon
                customblock_ext,                    # CustomBlock support
                kbd_ext,                            # Kbd support
                emo_ext,                            # Smileys support
                video_ext,                          # Video support
                preprocess_ext,                     # Preprocess support
                gridtable_ext,                      # Grid tables support
                comment_ext,                        # Comment support
                legend_ext,                         # Legend support
                align_ext,                          # Right align and center support
                dheader_ext,                        # Down Header support
                mathjax_ext,                        # Mathjax support
                ])
        md.registerExtensions(exts, {})
        if self.inline:
            #md.parser.blockprocessors.clear()
            md.preprocessors.pop("reference")
            md.inlinePatterns.pop("image_link")
            md.inlinePatterns.pop("image_reference")
            md.inlinePatterns.pop("reference")
            md.inlinePatterns.pop("short_reference")
            md.inlinePatterns.pop("linebreak")


def makeExtension(configs={}):
    return ZdsExtension(configs=dict(configs))


