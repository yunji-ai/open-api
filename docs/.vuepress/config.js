module.exports = {
    plugins: ['@vuepress/active-header-links', '@vuepress/back-to-top', '@vuepress/last-updated'],
    themeConfig: {
        sidebar: "auto",
        lastUpdated: 'Last Updated',
    },
    base: '/open-api/',
    markdown: {
        // markdown-it-anchor 的选项
        anchor: { permalink: false },
        // markdown-it-toc 的选项
        toc: { includeLevel: [1, 2] },
        extendMarkdown: md => {
            md.use(require('markdown-it-anchor'))
        }
    },
    evergreen: true,
    title: '机器人服务生开放接口文档',
}