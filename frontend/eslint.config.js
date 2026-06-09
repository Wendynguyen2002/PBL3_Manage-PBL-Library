import js from "@eslint/js";
import globals from "globals";
import pluginReact from "eslint-plugin-react";
import { defineConfig } from "eslint/config";

export default defineConfig([
  { files: ["**/*.{js,mjs,cjs,jsx}"], plugins: { js }, extends: ["js/recommended"], languageOptions: { globals: globals.browser } },
  pluginReact.configs.flat.recommended,      // Load recommended rules
  {
    files: ["**/*.{js,jsx}"],
    rules: {
      "react/react-in-jsx-scope": "off",     // Disable this rule (React 17+)
      "react/jsx-uses-react": "off",         // Also disable this one
    },
  },
]);