package android_serialport_api;

import java.util.List;  

import android.content.Context;  
import android.net.wifi.ScanResult;  
import android.net.wifi.WifiConfiguration;  
import android.net.wifi.WifiInfo;  
import android.net.wifi.WifiManager;  
import android.net.wifi.WifiManager.WifiLock;  
    
/**   

 */      
public class WifiAdmin {      
     
    private WifiManager mWifiManager = null;      
    private WifiInfo mWifiInfo = null;      
    private List<ScanResult> mWifiList = null;// ɨ��������������б�       
    private List<WifiConfiguration> mWifiConfiguration = null;// ���������б�       
    private WifiLock mWifiLock = null;      
     
    public WifiAdmin(Context mContext) {      
        mWifiManager = (WifiManager) mContext.getSystemService(mContext.WIFI_SERVICE);      
        mWifiInfo = mWifiManager.getConnectionInfo();      
    }      
     
    public boolean isWifiEnabled(){  
        return mWifiManager.isWifiEnabled();  
    }  
      
    public void OpenWifi() {      
        if (!mWifiManager.isWifiEnabled()) {      
            mWifiManager.setWifiEnabled(true);// ��wifi       
        }      
    }      
     
    public void CloseWife() {      
        if (mWifiManager.isWifiEnabled()) {      
            mWifiManager.setWifiEnabled(false);// �ر�wifi       
        }      
    }      
     
    public void lockWifi() {      
        mWifiLock.acquire();// ��wifi       
    }      
     
    public void rlockWifi() {      
        if (mWifiLock.isHeld()) {      
            mWifiLock.acquire();// ����wifi       
        }      
    }      
     
    public void Createwifilock() {      
        mWifiLock = mWifiManager.createWifiLock("Testss");// ����һ��wifilock       
    }      
     
    public List<WifiConfiguration> GetConfinguration() {      
        return mWifiConfiguration;// �õ����úõ�����       
    }      
     
    public void ConnectConfiguration(int index) {      
        if (index > mWifiConfiguration.size()) {      
            return;      
        }      
        mWifiManager.enableNetwork(mWifiConfiguration.get(index).networkId, true);//�������úõ�ָ��ID������       
    }      
    public void StartScan()      
    {      
        mWifiManager.startScan();      
        //�õ�ɨ����       
        mWifiList = mWifiManager.getScanResults();      
        //�õ����úõ���������       
        mWifiConfiguration = mWifiManager.getConfiguredNetworks();      
    }      
    //�õ������б�       
    public List<ScanResult> GetWifiList()      
    {      
        return mWifiList;      
    }      
    //�鿴ɨ����       
    public StringBuilder LookUpScan()      
    {      
        StringBuilder stringBuilder = new StringBuilder();      
        for (int i = 0; i < mWifiList.size(); i++)      
        {      
            stringBuilder.append("Search Result"+new Integer(i + 1).toString() + ":");      
            //��ScanResult��Ϣת����һ���ַ��       
            //���аѰ�����BSSID��SSID��capabilities��frequency��level       
            stringBuilder.append((mWifiList.get(i)).toString());      
            stringBuilder.append("\n");      
        }      
        return stringBuilder;      
    }      
    //�õ�MAC��ַ       
    public String GetMacAddress()      
    {      
        return (mWifiInfo == null) ? "NULL" : mWifiInfo.getMacAddress();      
    }      
    //�õ�������BSSID       
    public String GetBSSID()      
    {      
        return (mWifiInfo == null) ? "NULL" : mWifiInfo.getBSSID();      
    }      
    //�õ�IP��ַ       
    public int GetIPAddress()      
    {      
        return (mWifiInfo == null) ? 0 : mWifiInfo.getIpAddress();      
    }      
    //�õ����ӵ�ID       
    public int GetNetworkId()      
    {      
        return (mWifiInfo == null) ? 0 : mWifiInfo.getNetworkId();      
    }      
    //�õ�WifiInfo��������Ϣ��       
    public String GetWifiInfo()      
    {      
        return (mWifiInfo == null) ? "NULL" : mWifiInfo.toString();      
    }      
    //���һ�����粢����       
    public void AddNetwork(WifiConfiguration wcg)      
    {      
        int wcgID = mWifiManager.addNetwork(wcg);       
        mWifiManager.enableNetwork(wcgID, true);       
    }      
    //�Ͽ�ָ��ID������       
    public void DisconnectWifi(int netId)      
    {      
        mWifiManager.disableNetwork(netId);      
        mWifiManager.disconnect();      
    }      
}    
