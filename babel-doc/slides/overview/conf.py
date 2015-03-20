# -*- coding: utf-8 -*-
#
# -- General configuration -------------------------------------
import sys
import os

sys.path.insert(0, os.path.abspath('../../source/.sphinx/exts'))

source_suffix = '.rst'
master_doc = 'index'

project = u'Babel presentation'
copyright = u'Crossing-Tech SA'

version = '0.1.2'

# -- Options for HTML output -----------------------------------

extensions = ['sphinxjp.themecore',
              'sphinxcontrib.blockdiag',
              'sphinxcontrib.seqdiag',
              'includecode']

highlight_language = 'scala'

html_theme = 'impressjs'

html_use_index = False
