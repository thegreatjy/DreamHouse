package org.techtown.project_savedreamhouse;

public class House {
    String houseID;
    String houseGudong; //구,동
    String housePark; //주차장
    String houseJeonse; //전세
    String houseRent; //월세
    String houseSort; //오피스텔, 원룸
    String houseName; //이름
    String houseFloor; //층수
    String houseSpace1; //면적1
    String houseSpace2; //면적2
    String houseRegister; //등록일자

    public String getHouseID() {
        return houseID;
    }

    public void setHouseID(String houseID) {
        this.houseID = houseID;
    }

    public String getHouseGudong() {
        return houseGudong;
    }

    public void setHouseGudong(String houseGudong) {
        this.houseGudong = houseGudong;
    }

    public String getHousePark() {
        return housePark;
    }

    public void setHousePark(String housePark) {
        this.housePark = housePark;
    }

    public String getHouseJeonse() {
        return houseJeonse;
    }

    public void setHouseJeonse(String houseJeonse) {
        this.houseJeonse = houseJeonse;
    }

    public String getHouseRent() {
        return houseRent;
    }

    public void setHouseRent(String houseRent) {
        this.houseRent = houseRent;
    }

    public String getHouseSort() {
        return houseSort;
    }

    public void setHouseSort(String houseSort) {
        this.houseSort = houseSort;
    }

    public String getHouseName() {
        return houseName;
    }

    public void setHouseName(String houseName) {
        this.houseName = houseName;
    }

    public String getHouseFloor() {
        return houseFloor;
    }

    public void setHouseFloor(String houseFloor) {
        this.houseFloor = houseFloor;
    }

    public String getHouseSpace1() {
        return houseSpace1;
    }

    public void setHouseSpace1(String houseSpace1) {
        this.houseSpace1 = houseSpace1;
    }

    public String getHouseSpace2() {
        return houseSpace2;
    }

    public void setHouseSpace2(String houseSpace2) {
        this.houseSpace2 = houseSpace2;
    }

    public String getHouseRegister() {
        return houseRegister;
    }

    public void setHouseRegister(String houseRegister) {
        this.houseRegister = houseRegister;
    }

    public House(String houseID, String houseGudong, String housePark, String houseJeonse, String houseRent, String houseSort, String houseName, String houseFloor, String houseSpace1, String houseSpace2, String houseRegister) {
        this.houseGudong = houseGudong;
        this.housePark = housePark;
        this.houseJeonse = houseJeonse;
        this.houseRent = houseRent;
        this.houseSort = houseSort;
        this.houseName = houseName;
        this.houseFloor = houseFloor;
        this.houseSpace1 = houseSpace1;
        this.houseSpace2 = houseSpace2;
        this.houseRegister = houseRegister;
    }
    public House(String houseID){
        this.houseID = houseID;
    }
}