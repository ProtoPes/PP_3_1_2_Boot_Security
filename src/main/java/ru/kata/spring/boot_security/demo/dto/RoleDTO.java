package ru.kata.spring.boot_security.demo.dto;


public class RoleDTO implements BaseDTO {
    private long id;
    
    private String name;
    
    private String beautifulName;
    

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBeautifulName() {
        return beautifulName;
    }

    public void setBeautifulName(String beautifulName) {
        this.beautifulName = beautifulName;
    }
}
