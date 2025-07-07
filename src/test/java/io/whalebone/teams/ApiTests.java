package io.whalebone.teams;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.whalebone.teams.pojo.Team;
import io.whalebone.teams.utils.RosterScraper;

import static org.hamcrest.Matchers.equalTo;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

public class ApiTests {

    private static final String TEAMS_ENDPOINT = "https://qa-assignment.dev1.whalebone.io/api/teams";
    private static final int TEAM_COUNT = 32;
    private static final String OLDEST_TEAM = "Montreal Canadiens";
    private static final List<String> METROPOLITAN_TEAMS = List.of(
        "Carolina Hurricanes", 
        "Columbus Blue Jackets", 
        "New Jersey Devils", 
        "New York Islanders", 
        "New York Rangers", 
        "Philadelphia Flyers", 
        "Pittsburgh Penguins", 
        "Washington Capitals");

    // deserialize response body into a list of teams
    private List<Team> deserializeTeams(Response response) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode teamsListNode = mapper.readTree(response.asString()).get("teams");
        return mapper.readValue(teamsListNode.toString(), new TypeReference<>() {});
    }

    // picking the oldest team, used in multiple tests
    private Team getOldestTeam(List<Team> teams) {
        return teams.stream().min(Comparator.comparingInt(t -> t.getFounded())).orElseThrow(() -> new NoSuchElementException("No teams found"));
    }

    // verifies that tha API returns the expected amount of teams
    @Test
    public void testTeamCount() {
        RestAssured.get(TEAMS_ENDPOINT)
            .then()
            .statusCode(200)
            .body("teams.size()", equalTo(TEAM_COUNT));
    }

    // verifies expected name of the oldest team
    @Test
    public void testOldestTeam() {
        Response response = RestAssured.get(TEAMS_ENDPOINT)
            .then()
            .statusCode(200)
            .extract()
            .response();

        try {
            List<Team> teams = deserializeTeams(response);
            Team oldest = getOldestTeam(teams);
            Assert.assertEquals(OLDEST_TEAM, oldest.getName());

        } catch (JsonProcessingException e) {
            Assert.fail("Failed to process JSON response: " + e);
        }        
    }

    // verifies that there is a city with multiple teams and checks that the team names contain the name of the city
    @Test
    public void testCityWithMultipleTeams() {
        Response response = RestAssured.get(TEAMS_ENDPOINT)
            .then()
            .statusCode(200)
            .extract()
            .response();

        try {
            List<Team> teams = deserializeTeams(response);
            Map<String, List<Team>> cities = teams.stream().collect(Collectors.groupingBy(Team::getLocation));
            Map<String, List<Team>> multipleTeamCities = cities.entrySet().stream().filter(c -> c.getValue().size() > 1).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

            Assert.assertFalse(multipleTeamCities.isEmpty());
            multipleTeamCities.forEach((city, teamsList) -> {
                teamsList.forEach(team -> {
                    Assert.assertTrue(team.getName().contains(city));
                });
            });
        } catch (JsonProcessingException e) {
            Assert.fail("Failed to process JSON response: " + e);
        }  
    }

    // verifies the number of teams in metropolitan division and checks their names against a list of known teams
    @Test
    public void testMetropolitanDivision() {
        Response response = RestAssured.get(TEAMS_ENDPOINT)
            .then()
            .statusCode(200)
            .extract()
            .response();

        try {
            List<Team> teams = deserializeTeams(response);
            List<Team> metropolitanTeams = teams.stream().filter(team -> team.getDivision().getName().equalsIgnoreCase("metropolitan")).collect(Collectors.toList());

            Assert.assertEquals(8, metropolitanTeams.size());
            metropolitanTeams.forEach(team -> {
                Assert.assertTrue(METROPOLITAN_TEAMS.contains(team.getName()), "Unknown team in Metropolitan division: " + team.getName());
            });
        } catch (JsonProcessingException e) {
            Assert.fail("Failed to process JSON response: " + e);
        }  
    }

    // uses RosterScraper util to exctract number of Canadians and Americans from a team roster and verifies that there are more Canadians
    @Test
    public void testNumberOfCanadiansInOldestTeam() {
        Response response = RestAssured.get(TEAMS_ENDPOINT)
            .then()
            .statusCode(200)
            .extract()
            .response();

        try {
            List<Team> teams = deserializeTeams(response);
            Team oldestTeam = getOldestTeam(teams);
            Map<String, Integer> nationalities = RosterScraper.countCanadiansVsAmericans(oldestTeam.getOfficialSiteUrl() + "roster/");
            Assert.assertTrue(nationalities.get("CAN") > nationalities.get("USA"));
        } catch (JsonProcessingException e) {
            Assert.fail("Failed to process JSON response: " + e);
        }  
    }
}
