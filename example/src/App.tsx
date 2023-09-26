import * as React from 'react';

import { StyleSheet, View, Text, Image, FlatList, PermissionsAndroid, Button, Alert } from 'react-native';
import { isAppInstalled, OpenEditOfficeFileByWps, OpenReadonlyOfficeFileByWps, packageName_cn_wps_moffice_eng, packageName_com_kingsoft_moffice_pro } from 'react-native-wps';
import { DocumentDirectoryPath, DownloadDirectoryPath, exists, ExternalDirectoryPath, ExternalStorageDirectoryPath } from 'react-native-fs'
import { useUseDownloadApk } from './useDownloadApk';

export default function App() {
  const [result, setResult] = React.useState<number | undefined>();
  const [imgs, setImgs] = React.useState<string[]>([])



  React.useEffect(() => {
    async function x() {
      let permission = await PermissionsAndroid.request('android.permission.WRITE_EXTERNAL_STORAGE')
      if (permission !== 'granted') {
        throw '没有权限写入文件'
      }

      permission = await PermissionsAndroid.request('android.permission.READ_EXTERNAL_STORAGE')
      if (permission !== 'granted') {
        throw '没有权限读取文件'
      }


      /*console.log(ExternalDirectoryPath)
      try {

        console.log(ExternalDirectoryPath + "/77.pdf")
        //await OpenReadonlyOfficeFileByWps(ExternalDirectoryPath + "/99.doc", "22233")
      } catch (error) {
        console.log(error)
      }*/

      //const res = await PdfFiles("/data/user/0/com.wpsexample/cache/88.pdf")
      //console.log(res)
      // setImgs(res)
    }

    x()
  }, []);

  const openfile = async () => {
    try {
      await isAppInstalled(packageName_cn_wps_moffice_eng)
      console.log(ExternalDirectoryPath + "/a.docx")


      //await OpenReadonlyOfficeFileByWps(DocumentDirectoryPath + "/xxx.txt","application/msword")
      //console.log(await  exists(DocumentDirectoryPath + "/xxx.txt"))
      await OpenEditOfficeFileByWps(DocumentDirectoryPath + "/xxx.txt",
        '', '',
        ExternalDirectoryPath + "/a.docx",
        "application/msword", '')
      // await OpenReadonlyOfficeFileByWps('1', '2', '3', src, "application/msword",
      //   target)
    } catch (error) {
      Alert.alert('出错了')
      console.log(error)
    }
  }

  return (
    <View>
      <Button onPress={openfile} title='打开文件11'></Button>
      <FlatList
        keyExtractor={e => e}
        data={imgs}
        renderItem={(info) => <Image
          key={info.item}
          source={{ uri: 'file://' + info.item, width: 800, height: 600, cache: 'force-cache' }} />}></FlatList>

    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
  },
  box: {
    width: 60,
    height: 60,
    marginVertical: 20,
  },
});
