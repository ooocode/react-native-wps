import * as React from 'react';

import { StyleSheet, View, Image, FlatList, PermissionsAndroid, Button, Alert } from 'react-native';
import { isAppInstalled, OpenEditOfficeFileByWps, packageName_cn_wps_moffice_eng, registerMyTaskService, startMyTaskService } from 'react-native-wps';
import { DocumentDirectoryPath, ExternalDirectoryPath } from 'react-native-fs'


const sleep = (milliseconds: number) => {
  return new Promise(resolve => setTimeout(resolve, milliseconds));
};

registerMyTaskService(async (d) => {
  let count = 0
  while (true) {
    console.log(new Date().toLocaleTimeString() + '   ' + count)
    await sleep(1000)
    count++
    if (count > 10) {
      break
      //throw new Error('出错了')
    }
  }
})

/*startMyTaskService({
  qq: "123",
})*/

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
    startMyTaskService({
      qq: "123",
    })
    Alert.alert('启动成功')
    return
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
      <Button onPress={openfile} title='测试服务'></Button>
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
