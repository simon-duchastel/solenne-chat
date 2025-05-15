// {project}/webpack.config.d/sqljs.js
config.resolve = {
    fallback: {
        fs: false,
        path: false,
        crypto: false,
    }
};

const path = require('path');
const CopyWebpackPlugin = require('copy-webpack-plugin');
config.plugins.push(
    new CopyWebpackPlugin({
        patterns: [
            {
                from: path.resolve(__dirname, '../../../node_modules/sql.js/dist/sql-wasm.wasm'),
                to: '.'
            }
        ]
    })
);