# react-native-wps

内部测试项目使用，请勿下载



## Installation

```sh
yarn add react-native-wps

or

npm install react-native-wps
```

## Usage

```java
      @Nullable
      @Override
      protected String getJSBundleFile() {
        return UpdateContext.getJSBundleFile(MainApplication.this, "http://192.168.1.2:9999", "11", getUseDeveloperSupport());
      }
```

```js
import {  } from 'react-native-wps';

// ...

```

//package.json

 "bundle": "react-native bundle --platform android --entry-file index.js --bundle-output ./dist/index.android.bundle --assets-dest ./dist/ --dev false"


## Contributing

See the [contributing guide](CONTRIBUTING.md) to learn how to contribute to the repository and the development workflow.

## License

MIT

---

Made with [create-react-native-library](https://github.com/callstack/react-native-builder-bob)
