#/bin/bash
echo -n '从xx.scene拷贝到xx.anim:'
if cat /srv/ftp/pub/xx.scene > xx.anim
then
    echo -e "\t\033[32m成功\033[0m"
else
    echo -e "\t\033[32m失败\033[0m"
fi

echo -n "修改xx.anim的内容:"
if sed -n '/animations/, /animations/p' xx.anim > tmp.anim && mv tmp.anim xx.anim
then
    echo -e "\t\t\033[32m成功\033[0m"
else
    echo -e "\t\t\033[32m失败\033[0m"
fi

echo -n "关闭com.routon.jme_droid进程:"
if adb shell am force-stop com.routon.jme_droid
then
    echo -e "\t\033[32m成功\033[0m"
else
    echo -e "\t\033[32m失败\033[0m"
fi
