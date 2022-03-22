const path = require("path");

const root = path.resolve(process.cwd(), "scm-ui");

module.exports = {
  content: [path.join(root, "ui-webapp", "src", "**", "*.tsx"), path.join(root, "ui-components", "src", "**", "*.tsx")],
  theme: {
    extend: {}
  },
  plugins: []
};
