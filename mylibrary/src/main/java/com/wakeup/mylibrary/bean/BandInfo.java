package com.wakeup.mylibrary.bean;

public class BandInfo {
    private float firmwareVersionCode;
    private int bandVersionCode;
    private boolean canSetStepLength;
    private boolean canSetSleepTime;
    private boolean canSet12Hours;
    private boolean hasWeixinSport;
    private boolean hasHeartWarn;
    private boolean nordic;
    private boolean needPhoneSerialNumber;


    /**
     * 0x0C: 心电手环
     * 0x0B: 连续心率手环
     * 0x0D: 连续心率+血压手环
     * 0x0E: 连续心率+血压+血氧
     * 0x0F: 连续心率+血氧
     * 其他: 普通手环
     */
    private int bandType;

    private boolean hasPagesManager;
    private boolean hasInstagram;
    private boolean hasJiuzuotixing;
    /**
     * 连续心率省电模式
     */
    private boolean heartRateSaveBattery;

    /**
     * 连续心率红外参数设置
     */
    private boolean heartRateHongwai;

    /**
     * 加长消息内容长度，默认64字节
     */
    private boolean moreMessage;


    public float getFirmwareVersionCode() {
        return firmwareVersionCode;
    }

    public void setFirmwareVersionCode(float firmwareVersionCode) {
        this.firmwareVersionCode = firmwareVersionCode;
    }

    public int getBandVersionCode() {
        return bandVersionCode;
    }

    public void setBandVersionCode(int bandVersionCode) {
        this.bandVersionCode = bandVersionCode;
    }

    public boolean isCanSetStepLength() {
        return canSetStepLength;
    }

    public void setCanSetStepLength(boolean canSetStepLength) {
        this.canSetStepLength = canSetStepLength;
    }

    public boolean isCanSetSleepTime() {
        return canSetSleepTime;
    }

    public void setCanSetSleepTime(boolean canSetSleepTime) {
        this.canSetSleepTime = canSetSleepTime;
    }

    public boolean isCanSet12Hours() {
        return canSet12Hours;
    }

    public void setCanSet12Hours(boolean canSet12Hours) {
        this.canSet12Hours = canSet12Hours;
    }

    public boolean isHasWeixinSport() {
        return hasWeixinSport;
    }

    public void setHasWeixinSport(boolean hasWeixinSport) {
        this.hasWeixinSport = hasWeixinSport;
    }

    public boolean isHasHeartWarn() {
        return hasHeartWarn;
    }

    public void setHasHeartWarn(boolean hasHeartWarn) {
        this.hasHeartWarn = hasHeartWarn;
    }

    public boolean isNordic() {
        return nordic;
    }

    public void setNordic(boolean nordic) {
        this.nordic = nordic;
    }

    public boolean isNeedPhoneSerialNumber() {
        return needPhoneSerialNumber;
    }

    public void setNeedPhoneSerialNumber(boolean needPhoneSerialNumber) {
        this.needPhoneSerialNumber = needPhoneSerialNumber;
    }

    public int getBandType() {
        return bandType;
    }

    public void setBandType(int bandType) {
        this.bandType = bandType;
    }

    public boolean isHasPagesManager() {
        return hasPagesManager;
    }

    public void setHasPagesManager(boolean hasPagesManager) {
        this.hasPagesManager = hasPagesManager;
    }

    public boolean isHasInstagram() {
        return hasInstagram;
    }

    public void setHasInstagram(boolean hasInstagram) {
        this.hasInstagram = hasInstagram;
    }

    public boolean isHasJiuzuotixing() {
        return hasJiuzuotixing;
    }

    public void setHasJiuzuotixing(boolean hasJiuzuotixing) {
        this.hasJiuzuotixing = hasJiuzuotixing;
    }

    public boolean isHeartRateSaveBattery() {
        return heartRateSaveBattery;
    }

    public void setHeartRateSaveBattery(boolean heartRateSaveBattery) {
        this.heartRateSaveBattery = heartRateSaveBattery;
    }

    public boolean isHeartRateHongwai() {
        return heartRateHongwai;
    }

    public void setHeartRateHongwai(boolean heartRateHongwai) {
        this.heartRateHongwai = heartRateHongwai;
    }

    public boolean isMoreMessage() {
        return moreMessage;
    }

    public void setMoreMessage(boolean moreMessage) {
        this.moreMessage = moreMessage;
    }

    @Override
    public String toString() {
        return "BandInfo{" +
                "firmwareVersionCode=" + firmwareVersionCode +
                ", bandVersionCode=" + bandVersionCode +
                ", canSetStepLength=" + canSetStepLength +
                ", canSetSleepTime=" + canSetSleepTime +
                ", canSet12Hours=" + canSet12Hours +
                ", hasWeixinSport=" + hasWeixinSport +
                ", hasHeartWarn=" + hasHeartWarn +
                ", nordic=" + nordic +
                ", needPhoneSerialNumber=" + needPhoneSerialNumber +
                ", bandType=" + bandType +
                ", hasPagesManager=" + hasPagesManager +
                ", hasInstagram=" + hasInstagram +
                ", hasJiuzuotixing=" + hasJiuzuotixing +
                ", heartRateSaveBattery=" + heartRateSaveBattery +
                ", heartRateHongwai=" + heartRateHongwai +
                ", moreMessage=" + moreMessage +
                '}';
    }
}
