# -*- coding: utf-8 -*-

# Zest Writer documentation build configuration file, created by
# sphinx-quickstart on Thu Sep 22 00:12:55 2016.
#
# This file is execfile()d with the current directory set to its
# containing dir.
#
# Note that not all possible configuration values are present in this
# autogenerated file.
#
# All configuration values have a default; values that are commented out
# serve to show the default.

# If extensions (or modules to document with autodoc) are in another directory,
# add these directories to sys.path here. If the directory is relative to the
# documentation root, use os.path.abspath to make it absolute, like shown here.
#
# import os
# import sys
# sys.path.insert(0, os.path.abspath('.'))

# -- General configuration ------------------------------------------------

# If your documentation needs a minimal Sphinx version, state it here.
#
# needs_sphinx = '1.0'

import sys
import os
from datetime import datetime
from shutil import copyfile, rmtree
import os.path as op
from subprocess import Popen, PIPE

ON_RTD = os.environ.get('READTHEDOCS', None) == 'True'

BUILD_DIR = './build/'
SOURCE_DIR = '../src/main/java/'
BUILD_RST_DIR = BUILD_DIR + 'rst/'
JAVADOC_RST_DIR = BUILD_RST_DIR + 'javadoc/'
README_PATH = '../readme.rst'
RST_DIR = './rst/'
RTD_README = RST_DIR + '.presentation.rst'
GITHUB_RAW_PATH = 'https://github.com/firm1/zest-writer/blob/master/'

if op.exists(BUILD_DIR):
    rmtree(BUILD_DIR)
os.makedirs(BUILD_RST_DIR)

def get_version():
    '''Returns project version as string from 'git describe' command.'''


    pipe = Popen('git describe --tags --always', stdout=PIPE, shell=True)
    desc = pipe.stdout.read().decode('utf-8')

    if desc:
        return desc.split('-', 1)[0], desc
    else:
        return u'unknown', u'unknown'

def copy_readme():
    print '\n*** copying readme file ***\n'
    with open(README_PATH) as infile, open(RTD_README, 'w') as outfile:
        print_on_rtd = True
        for i, line in enumerate(infile.readlines()):
            line = line.replace('<./', '<//')
            line = line.replace('./doc/images/', '../../images/')
            if line.startswith('.. no_rtd'):
                print_on_rtd = False
            if line.startswith('.. rtd'):
                print_on_rtd = True

            if i==3:
                line = 'Présentation\n'

            if print_on_rtd:
                outfile.write(line)

def copy_rst():
    print '\n*** copying rst files ***\n'

    rst_names = [w[2] for w in os.walk(RST_DIR)][0]

    print 'rst file names: ', rst_names

    for rst_name in rst_names:
        with open(RST_DIR + rst_name) as infile, open(BUILD_RST_DIR + rst_name, 'w') as outfile:
            for line in infile.readlines():
                line = line.replace('<\//', '[URL_RESOLVER_ESCAPE]')
                line = line.replace('<//doc/', '<../../../../')
                line = line.replace('<//', '<%s' % GITHUB_RAW_PATH if ON_RTD else '<../../../../../')
                line = line.replace('[URL_RESOLVER_ESCAPE]', '<//')
                outfile.write(line)

def javadoc_build():
    print '\n*** building javadoc ***\n'
    from javasphinx.apidoc import main as build_doc

    javasphinx_params = ['javasphinx-apidoc', '-f', '-o', JAVADOC_RST_DIR, SOURCE_DIR]

    print(' '.join(javasphinx_params))
    build_doc(javasphinx_params)

if not ON_RTD:  # only import and set the theme if we're building docs locally
    import sphinx_rtd_theme
    html_theme = 'sphinx_rtd_theme'
    html_theme_path = [sphinx_rtd_theme.get_html_theme_path()]

copy_readme()
copy_rst()
javadoc_build()

# Add any Sphinx extension module names here, as strings. They can be
# extensions coming with Sphinx (named 'sphinx.ext.*') or your custom
# ones.
extensions = [
    'sphinx.ext.todo',
    'javasphinx',
]

# JavaSphinx configuration
# javadoc_url_map = {
#     # Add here other Java package if necessary.
# }

# Add any paths that contain templates here, relative to this directory.
templates_path = ['_templates']

# The suffix(es) of source filenames.
# You can specify multiple suffix as a list of string:
#
source_suffix = ['.rst']

# The encoding of source files.
#
source_encoding = 'utf-8'

# The master toctree document.
master_doc = 'index'

# General information about the project.
project = u'Zest Writer'
author = u'firm1 and other contributors'
copyright = u'%s, %s' % (datetime.now().year, author)

# The version info for the project you're documenting, acts as replacement for
# |version| and |release|, also used in various other places throughout the
# built documents.
#
# The short X.Y version.
version, release = get_version()

# The language for content autogenerated by Sphinx. Refer to documentation
# for a list of supported languages.
#
# This is also used if you do content translation via gettext catalogs.
# Usually you set "language" from the command line for these cases.
language = 'fr'

# There are two options for replacing |today|: either, you set today to some
# non-false value, then it is used:
#
# today = ''
#
# Else, today_fmt is used as the format for a strftime call.
#
today_fmt = '%d/%m/%Y'

# List of patterns, relative to source directory, that match files and
# directories to ignore when looking for source files.
# This patterns also effect to html_static_path and html_extra_path
exclude_patterns = ['rst']

# The reST default role (used for this markup: `text`) to use for all
# documents.
#
# default_role = None

# If true, '()' will be appended to :func: etc. cross-reference text.
#
# add_function_parentheses = True

# If true, the current module name will be prepended to all description
# unit titles (such as .. function::).
#
# add_module_names = True

# If true, sectionauthor and moduleauthor directives will be shown in the
# output. They are ignored by default.
#
# show_authors = False

# The name of the Pygments (syntax highlighting) style to use.
pygments_style = 'sphinx'

# A list of ignored prefixes for module index sorting.
# modindex_common_prefix = []

# If true, keep warnings as "system message" paragraphs in the built documents.
# keep_warnings = False

# If true, `todo` and `todoList` produce output, else they produce nothing.
todo_include_todos = True


# -- Options for HTML output ----------------------------------------------

# The theme to use for HTML and HTML Help pages.  See the documentation for
# a list of builtin themes.
# html_theme = 'sphinx_rtd_theme'

# Theme options are theme-specific and customize the look and feel of a theme
# further.  For a list of options available for each theme, see the
# documentation.
#
# html_theme_options = {}

# Add any paths that contain custom themes here, relative to this directory.
# html_theme_path = []

# The name for this set of Sphinx documents.
# "<project> v<release> documentation" by default.
#
html_title = u'Documentation de %s %s' % (project, version)

# A shorter title for the navigation bar.  Default is the same as html_title.
#
html_short_title = html_title

# The name of an image file (relative to this directory) to place at the top
# of the sidebar.
#
html_logo = 'images/logo.png'

# The name of an image file (relative to this directory) to use as a favicon of
# the docs.  This file should be a Windows icon file (.ico) being 16x16 or 32x32
# pixels large.
#
html_favicon = 'images/logo.ico'

# Add any paths that contain custom static files (such as style sheets) here,
# relative to this directory. They are copied after the builtin static files,
# so a file named "default.css" will overwrite the builtin "default.css".
# html_static_path = ['_static']

# Add any extra paths that contain custom files (such as robots.txt or
# .htaccess) here, relative to this directory. These files are copied
# directly to the root of the documentation.
#
# html_extra_path = []

# If not None, a 'Last updated on:' timestamp is inserted at every page
# bottom, using the given strftime format.
# The empty string is equivalent to '%b %d, %Y'.
#
html_last_updated_fmt = ''

# If true, SmartyPants will be used to convert quotes and dashes to
# typographically correct entities.
#
html_use_smartypants = True

# Custom sidebar templates, maps document names to template names.
#
# html_sidebars = {}

# Additional templates that should be rendered to pages, maps page names to
# template names.
#
# html_additional_pages = {}

# If false, no module index is generated.
#
html_domain_indices = False

# If false, no index is generated.
#
html_use_index = True

# If true, the index is split into individual pages for each letter.
#
html_split_index = False

# If true, links to the reST sources are added to the pages.
#
html_show_sourcelink = True

# If true, "Created using Sphinx" is shown in the HTML footer. Default is True.
#
html_show_sphinx = True

# If true, "(C) Copyright ..." is shown in the HTML footer. Default is True.
#
html_show_copyright = True

# If true, an OpenSearch description file will be output, and all pages will
# contain a <link> tag referring to it.  The value of this option must be the
# base URL from which the finished HTML is served.
#
# html_use_opensearch = ''

# This is the file name suffix for HTML files (e.g. ".xhtml").
# html_file_suffix = None

# Language to be used for generating the HTML full-text search index.
# Sphinx supports the following languages:
#   'da', 'de', 'en', 'es', 'fi', 'fr', 'hu', 'it', 'ja'
#   'nl', 'no', 'pt', 'ro', 'ru', 'sv', 'tr', 'zh'
#
html_search_language = 'fr'

# A dictionary with options for the search language support, empty by default.
# 'ja' uses this config value.
# 'zh' user can custom change `jieba` dictionary path.
#
# html_search_options = {'type': 'default'}

# The name of a javascript file (relative to the configuration directory) that
# implements a search results scorer. If empty, the default will be used.
#
# html_search_scorer = 'scorer.js'

# Output file base name for HTML help builder.
htmlhelp_basename = 'ZestWriterdoc'

# -- Options for LaTeX output ---------------------------------------------

latex_elements = {
     # The paper size ('letterpaper' or 'a4paper').
     #
     # 'papersize': 'letterpaper',

     # The font size ('10pt', '11pt' or '12pt').
     #
     # 'pointsize': '10pt',

     # Additional stuff for the LaTeX preamble.
     #
     # 'preamble': '',

     # Latex figure (float) alignment
     #
     # 'figure_align': 'htbp',
}

# Grouping the document tree into LaTeX files. List of tuples
# (source start file, target name, title,
#  author, documentclass [howto, manual, or own class]).
latex_documents = [
    (master_doc, 'ZestWriter.tex', u'Zest Writer Documentation',
     u'firm1', 'manual'),
]

# The name of an image file (relative to this directory) to place at the top of
# the title page.
#
latex_logo = 'images/logo.png'

# For "manual" documents, if this is true, then toplevel headings are parts,
# not chapters.
#
# latex_use_parts = False

# If true, show page references after internal links.
#
# latex_show_pagerefs = False

# If true, show URL addresses after external links.
#
# latex_show_urls = False

# Documents to append as an appendix to all manuals.
#
# latex_appendices = []

# It false, will not define \strong, \code, 	itleref, \crossref ... but only
# \sphinxstrong, ..., \sphinxtitleref, ... To help avoid clash with user added
# packages.
#
# latex_keep_old_macro_names = True

# If false, no module index is generated.
#
# latex_domain_indices = True


# -- Options for manual page output ---------------------------------------

# One entry per manual page. List of tuples
# (source start file, name, description, authors, manual section).
man_pages = [
    (master_doc, 'zestwriter', u'Zest Writer Documentation',
     [author], 1)
]

# If true, show URL addresses after external links.
#
# man_show_urls = False


# -- Options for Texinfo output -------------------------------------------

# Grouping the document tree into Texinfo files. List of tuples
# (source start file, target name, title, author,
#  dir menu entry, description, category)
texinfo_documents = [
    (master_doc, 'ZestWriter', u'Zest Writer Documentation',
     author, 'ZestWriter', 'One line description of project.',
     'Miscellaneous'),
]

# Documents to append as an appendix to all manuals.
#
# texinfo_appendices = []

# If false, no module index is generated.
#
# texinfo_domain_indices = True

# How to display URL addresses: 'footnote', 'no', or 'inline'.
#
texinfo_show_urls = 'footnote'

# If true, do not generate a @detailmenu in the "Top" node's menu.
#
# texinfo_no_detailmenu = False
