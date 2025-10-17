package sptech.school;

import java.time.LocalDate;

public class Jogador {
    private String nickName;
    private String fullName;
    private LocalDate birthDate;
    private int age;
    private String country;
    private String team;
    private String role;
    private String liquipediaLink;
    private String twitter;
    private String twitch;
    private String instagram;
    private Double totalWinnings;
    private Integer regiao;
    private Integer eloDivisao;
    private Integer elo;

    public Jogador(String nickName, String fullName, LocalDate birthDate, int age, String country, String team, String role, String liquipediaLink, String twitter, String twitch, String instagram, Double totalWinnings, Integer regiao, Integer eloDivisao, Integer elo) {
        this.nickName = nickName;
        this.fullName = fullName;
        this.birthDate = birthDate;
        this.age = age;
        this.country = country;
        this.team = team;
        this.role = role;
        this.liquipediaLink = liquipediaLink;
        this.twitter = twitter;
        this.twitch = twitch;
        this.instagram = instagram;
        this.totalWinnings = totalWinnings;
        this.regiao = regiao;
        this.eloDivisao = eloDivisao;
        this.elo = elo;
    }

    public String getNickName() {
        return nickName;
    }

    public String getFullName() {
        return fullName;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public int getAge() {
        return age;
    }

    public String getCountry() {
        return country;
    }

    public String getTeam() {
        return team;
    }

    public String getRole() {
        return role;
    }

    public String getLiquipediaLink() {
        return liquipediaLink;
    }

    public String getTwitter() {
        return twitter;
    }

    public String getTwitch() {
        return twitch;
    }

    public String getInstagram() {
        return instagram;
    }

    public Double getTotalWinnings() {
        return totalWinnings;
    }

    public Integer getRegiao() {
        return regiao;
    }

    public Integer getEloDivisao() {
        return eloDivisao;
    }

    public Integer getElo() {
        return elo;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setTeam(String team) {
        this.team = team;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setLiquipediaLink(String liquipediaLink) {
        this.liquipediaLink = liquipediaLink;
    }

    public void setTwitter(String twitter) {
        this.twitter = twitter;
    }

    public void setTwitch(String twitch) {
        this.twitch = twitch;
    }

    public void setInstagram(String instagram) {
        this.instagram = instagram;
    }

    public void setTotalWinnings(Double totalWinnings) {
        this.totalWinnings = totalWinnings;
    }

    public void setRegiao(Integer regiao) {
        this.regiao = regiao;
    }

    public void setEloDivisao(Integer eloDivisao) {
        this.eloDivisao = eloDivisao;
    }

    public void setElo(Integer elo) {
        this.elo = elo;
    }

    @Override
    public String toString() {
        return "Jogador{" +
                "nickName='" + nickName + '\'' +
                ", fullName='" + fullName + '\'' +
                ", birthDate=" + birthDate +
                ", age=" + age +
                ", country='" + country + '\'' +
                ", team='" + team + '\'' +
                ", role='" + role + '\'' +
                ", liquipediaLink='" + liquipediaLink + '\'' +
                ", twitter='" + twitter + '\'' +
                ", twitch='" + twitch + '\'' +
                ", instagram='" + instagram + '\'' +
                ", totalWinnings=" + totalWinnings +
                ", regiao=" + regiao +
                ", eloDivisao=" + eloDivisao +
                ", elo=" + elo +
                '}';
    }
}
