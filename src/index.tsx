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


/**
 * 获取index.bundle 版本，调试模式为空
 * @returns 
 */
export const getBundleVersion = async () => {
  const version = await Wps.getBundleVersion()
  return version as string;
}

/**
 * 打开本地文件
 * @param path 
 * @param contentType 
 * @returns 
 */
export async function OpenLocalFile(path: string, contentType: string) {
  const res = await Wps.OpenLocalFile(path, contentType);
  return res as boolean
}

/**
 * 通过WPS打开文件（只读模式）
 * @param path 
 * @param contentType 
 * @returns 
 */
export async function OpenReadonlyOfficeFileByWps(path: string, contentType: string) {
  const res = await Wps.OpenReadonlyOfficeFileByWps(path, contentType);
  return res as boolean
}


/**
 * 通过WPS打开文件（编辑模式）
 * @param path 
 * @param contentType 
 * @returns 
 */
export async function OpenEditOfficeFileByWps(
  workFlowBaseUrl: string,
  token: string,
  userName: string,
  path: string, contentType: string,
  savePath: string) {
  const res = await Wps.OpenEditOfficeFileByWps(workFlowBaseUrl, token, userName, path, contentType, savePath);
  return res as boolean
}


/**
 * 安装APK
 * @param fileName 
 * @returns 
 */
export async function installApk(fileName: string) {
  const res = await Wps.installApk(fileName);
  return res as void
}

/**
 * 是否安装了包
 * @param packageName 
 * @returns 
 */
export async function isAppInstalled(packageName: string) {
  const res = await Wps.isAppInstalled(packageName);
  return res as boolean
}

/**
 * WPS专业版包名
 */
export const packageName_com_kingsoft_moffice_pro = 'com.kingsoft.moffice_pro'

/**
 * WPS普通版包名
 */
export const packageName_cn_wps_moffice_eng = 'cn.wps.moffice_eng'