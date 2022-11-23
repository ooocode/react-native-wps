import * as React from 'react';

import { StyleSheet, View, Text, Image, FlatList } from 'react-native';
import { multiply, PdfFiles } from 'react-native-wps';

export default function App() {
  const [result, setResult] = React.useState<number | undefined>();
  const [imgs, setImgs] = React.useState<string[]>([])


  React.useEffect(() => {
    async function x() {
      const res = await PdfFiles("/data/user/0/com.wpsexample/cache/88.pdf")
      console.log(res)
      setImgs(res)
    }

    x()
    multiply(3, 7).then(setResult);
  }, []);

  return (
    <View>
      <FlatList
        keyExtractor={e => e}
        data={imgs}
        renderItem={(info) => <Image
          key={info.item}
          source={{ uri: 'file://' + info.item, width: 800, height: 600,cache:'force-cache' }} />}></FlatList>

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
