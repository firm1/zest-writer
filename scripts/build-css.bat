set ZDS_VERSION="v29.1a-plume"

IF EXIST .\zds-site (
rmdir .\zds-site /s /q
)

git clone  --branch "%ZDS_VERSION%" https://github.com/zestedesavoir/zds-site.git

cd zds-site\

call npm install
call npm run build

xcopy .\dist ..\..\src\main\resources\com\zds\zw\assets\dist\ /E /y /Q

cd ..

@rem rmdir .\zds-site /s /q