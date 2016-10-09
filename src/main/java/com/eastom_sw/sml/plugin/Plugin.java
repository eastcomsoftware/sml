package com.eastom_sw.sml.plugin;

public interface Plugin {
    /**
     * 初始化插件
     */
    void init();

    /**
     * 销毁插件
     */
    void destroy();
}
