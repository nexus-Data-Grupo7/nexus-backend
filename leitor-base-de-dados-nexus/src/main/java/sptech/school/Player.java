package sptech.school;

import java.time.LocalDate;

public class Player {

    private String nick_name;
    private String full_name;
    private LocalDate birth_date;
    private Integer age;
    private String country_full;
    private String team;
    private String role;

    public Player() {

    }

    public Player(String nick_name, String full_name, LocalDate birth_date, Integer age, String country_full, String team, String role) {
        this.nick_name = nick_name;
        this.full_name = full_name;
        this.birth_date = birth_date;
        this.age = age;
        this.country_full = country_full;
        this.team = team;
        this.role = role;
    }

    public String getNick_name() {
        return nick_name;
    }

    public void setNick_name(String nick_name) {
        this.nick_name = nick_name;
    }

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public LocalDate getBirth_date() {
        return birth_date;
    }

    public void setBirth_date(LocalDate birth_date) {
        this.birth_date = birth_date;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getCountry_full() {
        return country_full;
    }

    public void setCountry_full(String country_full) {
        this.country_full = country_full;
    }

    public String getTeam() {
        return team;
    }

    public void setTeam(String team) {
        this.team = team;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "Player{" +
                "nick_name='" + nick_name + '\'' +
                ", full_name='" + full_name + '\'' +
                ", birth_date=" + birth_date +
                ", age=" + age +
                ", country_full='" + country_full + '\'' +
                ", team='" + team + '\'' +
                ", role='" + role + '\'' +
                '}';
    }

}
