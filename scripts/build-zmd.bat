set ZMD_VERSION="9.1.3"

IF EXIST .\zmarkdown (
rmdir .\zmarkdown /s /q
)

git clone  --branch "zmarkdown@%ZMD_VERSION%" https://github.com/zestedesavoir/zmarkdown.git

cd zmarkdown\

call npm install

cd packages\zmarkdown\

move /y package.json package.json.old
move /y common.js common.js.old

type package.json.old | findstr /v remark-iframes > package.json
type common.js.old | findstr /v remarkIframes > common.js

call npm install

call npm run release

xcopy dist\*.js ..\..\..\..\src\main\resources\com\zds\zw\js\

cd ..\..\..

rmdir .\zmarkdown /s /q