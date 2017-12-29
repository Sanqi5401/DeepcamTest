package com.deepcam.access.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by zsq on 17-12-29.
 */


@Entity
public class UserInfo {

    @Id(autoincrement = true)
    private Long id;

    @Property(nameInDb = "USERNAME")
    private String name;

    private String employeeNumber;

    private String sector;

    private String facePath;

    private String gender;

    @NotNull
    private String feature;

    @Generated(hash = 1826438847)
    public UserInfo(Long id, String name, String employeeNumber, String sector,
            String facePath, String gender, @NotNull String feature) {
        this.id = id;
        this.name = name;
        this.employeeNumber = employeeNumber;
        this.sector = sector;
        this.facePath = facePath;
        this.gender = gender;
        this.feature = feature;
    }

    @Generated(hash = 1279772520)
    public UserInfo() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFeature() {
        return feature;
    }

    public void setFeature(String feature) {
        this.feature = feature;
    }

    public String getEmployeeNumber() {
        return employeeNumber;
    }

    public void setEmployeeNumber(String employeeNumber) {
        this.employeeNumber = employeeNumber;
    }

    public String getSector() {
        return sector;
    }

    public void setSector(String sector) {
        this.sector = sector;
    }

    public String getFacePath() {
        return facePath;
    }

    public void setFacePath(String facePath) {
        this.facePath = facePath;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}
