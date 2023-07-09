import { useCallback, useState } from "react"
import { Alert } from "react-native"
import { downloadFile, ExternalDirectoryPath } from "react-native-fs"
import { installApk } from 'react-native-wps'

const url = 'http://192.168.1.3:9888/debug/app-debug.apk'
const toFile = `${ExternalDirectoryPath}/app-download.apk`


export function useUseDownloadApk() {
    const [percent, setPercent] = useState(0)
    const [isDownloading, setDownloading] = useState(false)

    const startDownloadApk = useCallback(async () => {
        setPercent(0)
        setDownloading(true)

        try {
            const { jobId, promise } = downloadFile({
                fromUrl: url,
                toFile: toFile,
                progressInterval: 1000,
                progress: (res => {
                    let p = parseInt((res.bytesWritten / res.contentLength * 100).toString())
                    setPercent(p)
                })
            })

            let res = await promise
            setPercent(100)
            setDownloading(false)
            console.log('下载完成', url, toFile)
            return true
        } catch (error) {
            if (error instanceof Error) {
                Alert.alert('更新APP出错', error.message)
            } else {
                Alert.alert(JSON.stringify(error))
            }
        }
        setDownloading(false)
        return false
    }, [])

    const startInstallApk = async () => {
        console.log("开始安装：" + toFile)
        var result = await installApk(toFile)
        console.log('apk安装结果', result)
    }

    return {
        startDownloadApk,
        isDownloading,
        startInstallApk,
        percent
    }
}