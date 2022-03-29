const path = require("path");

module.exports = {
  typescript: { reactDocgen: false },
  core: {
    builder: "webpack5"
  },
  stories: ["../src/**/*.stories.mdx", "../src/**/*.stories.@(js|jsx|ts|tsx)"],
  addons: ["@storybook/addon-links", "@storybook/addon-essentials", "@storybook/addon-interactions"],
  framework: "@storybook/react",
  webpackFinal: async config => {
    config.module.rules.push({
      test: /\.css$/,
      use: [
        {
          loader: "postcss-loader",
          options: {
            postcssOptions: {
              plugins: [require("tailwindcss"), require("autoprefixer")]
            }
          }
        }
      ],
      include: path.resolve(__dirname, "../")
    });

    return config;
  }
};
