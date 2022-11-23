# react-native-wps

内部测试项目使用，请勿下载

```java
      @Nullable
      @Override
      protected String getJSBundleFile() {
        return UpdateContext.getJSBundleFile(MainApplication.this, "http://192.168.1.2:9999", "11", getUseDeveloperSupport());
      }
```

## Installation

```sh
npm install react-native-wps
```

## Usage

```js
import { multiply } from 'react-native-wps';

// ...

const result = await multiply(3, 7);
```

## Contributing

See the [contributing guide](CONTRIBUTING.md) to learn how to contribute to the repository and the development workflow.

## License

MIT

---

Made with [create-react-native-library](https://github.com/callstack/react-native-builder-bob)
