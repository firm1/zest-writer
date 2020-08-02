ZMarkdown.use(ZMarkdownZHTML);
ZMarkdown.setDefaultModule('zhtml');
var toHtml = function (markdown) {
    var html = '';
    ZMarkdownZHTML.render(markdown, function (err, vfile) {
        html = vfile;
    });
    return ''+html+'';
}