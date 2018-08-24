package org.neo.entity;

public class People {

    private String name;
    private int age;
    private String masterpiece;

    public People(){

    }

    public People(String name, int age, String masterpiece) {
        this.name = name;
        this.age = age;
        this.masterpiece = masterpiece;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getMasterpiece() {
        return masterpiece;
    }

    public void setMasterpiece(String masterpiece) {
        this.masterpiece = masterpiece;
    }
}
