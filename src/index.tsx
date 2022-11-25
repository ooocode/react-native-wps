import { NativeModules, Platform } from 'react-native';

const LINKING_ERROR =
  `The package 'react-native-wps' doesn't seem to be linked. Make sure: \n\n` +
  Platform.select({ ios: "- You have run 'pod install'\n", default: '' }) +
  '- You rebuilt the app after installing the package\n' +
  '- You are not using Expo Go\n';

const Wps = NativeModules.Wps
  ? NativeModules.Wps
  : new Proxy(
    {},
    {
      get() {
        throw new Error(LINKING_ERROR);
      },
    }
  );


export async function OpenReadonlyOfficeFileByWps(path: string, contentType: string) {
  const res = await Wps.OpenReadonlyOfficeFileByWps(path, contentType);
  return res
}
