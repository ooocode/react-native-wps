import * as React from 'react';

import { StyleSheet, View, Text, Image, FlatList, PermissionsAndroid } from 'react-native';
import { OpenReadonlyOfficeFileByWps } from 'react-native-wps';
import { DownloadDirectoryPath, ExternalDirectoryPath } from 'react-native-fs'

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


      console.log(ExternalDirectoryPath)
      try {
        await OpenReadonlyOfficeFileByWps(ExternalDirectoryPath + "/77.pdf", "22233")
      } catch (error) {
        console.log(error)
      }

      //const res = await PdfFiles("/data/user/0/com.wpsexample/cache/88.pdf")
      //console.log(res)
      // setImgs(res)
    }

    x()
  }, []);

  return (
    <View>
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
