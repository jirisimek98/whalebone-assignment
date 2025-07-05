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

import static org.hamcrest.Matchers.equalTo;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
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

    private List<Team> deserializeTeams(Response response) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode teamsListNode = mapper.readTree(response.asString()).get("teams");
        return mapper.readValue(teamsListNode.toString(), new TypeReference<>() {});
    }

    @Test
    public void testTeamCount() {
        RestAssured.get(TEAMS_ENDPOINT)
            .then()
            .statusCode(200)
            .body("teams.size()", equalTo(TEAM_COUNT));
    }

    @Test
    public void testOldestTeam() {
        Response response = RestAssured.get(TEAMS_ENDPOINT)
            .then()
            .statusCode(200)
            .extract()
            .response();

        try {
            List<Team> teams = deserializeTeams(response);
            Team oldest = teams.stream()
                .min(Comparator.comparingInt(t -> t.getFounded()))
                .orElseThrow(() -> new AssertionError());
            Assert.assertEquals(OLDEST_TEAM, oldest.getName());

        } catch (JsonProcessingException e) {
            Assert.fail("Failed to process JSON response: " + e);
        }        
    }

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
}
