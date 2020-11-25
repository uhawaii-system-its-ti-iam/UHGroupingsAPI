package edu.hawaii.its.api.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import edu.hawaii.its.api.configuration.SpringBootWebApplication;
import edu.hawaii.its.api.repository.GroupRepository;
import edu.hawaii.its.api.type.AdminListsHolder;
import edu.hawaii.its.api.type.Group;
import edu.hawaii.its.api.type.Grouping;
import edu.hawaii.its.api.type.GroupingAssignment;
import edu.hawaii.its.api.type.MembershipAssignment;
import edu.hawaii.its.api.type.Person;

import edu.internet2.middleware.grouperClient.ws.beans.WsGetMembersResult;
import edu.internet2.middleware.grouperClient.ws.beans.WsGetMembersResults;
import edu.internet2.middleware.grouperClient.ws.beans.WsGroup;
import edu.internet2.middleware.grouperClient.ws.beans.WsSubject;
import edu.internet2.middleware.grouperClient.ws.beans.WsSubjectLookup;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@ActiveProfiles("localTest")
@RunWith(SpringRunner.class)
@SpringBootTest(classes = { SpringBootWebApplication.class })
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class GroupingAssignmentServiceTest {

    private static final String PATH_ROOT = "path:to:grouping";
    private static final String BASIS = ":basis";
    private static final String GROUPING_0_PATH = PATH_ROOT + 0;
    private static final String GROUPING_1_PATH = PATH_ROOT + 1;
    private static final String GROUPING_3_PATH = PATH_ROOT + 3;
    private static final String GROUPING_3_BASIS_PATH = GROUPING_3_PATH + BASIS;

    private static final String ADMIN_USER = "admin";
    private List<Person> admins = new ArrayList<>();
    private Group adminGroup = new Group();
    private Group appGroup = new Group();
    private List<Person> users = new ArrayList<>();
    private List<WsSubjectLookup> lookups = new ArrayList<>();

    @Autowired
    private GrouperConfiguration grouperConfiguration;
   
    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private GroupingAssignmentService groupingAssignmentService;

    @Autowired
    private MembershipService membershipService;

    @Autowired
    private DatabaseSetupService databaseSetupService;

    @Before
    public void setup() {
        databaseSetupService.initialize(users, lookups, admins, adminGroup, appGroup);
    }

    @Test
    public void construction() {
        assertNotNull(groupingAssignmentService);
    }

    @Test
    public void getGroupingTest() {
        try {
            groupingAssignmentService.getGrouping(GROUPING_0_PATH, users.get(1).getUsername());
        } catch (AccessDeniedException ade) {
            assertThat(grouperConfiguration.getInsufficientPrivileges(), is(ade.getMessage()));
        }
        Grouping groupingOwner = groupingAssignmentService.getGrouping(GROUPING_0_PATH, users.get(0).getUsername());
        Grouping groupingAdmin = groupingAssignmentService.getGrouping(GROUPING_0_PATH, ADMIN_USER);

        assertTrue(groupingOwner.getComposite().getNames().contains(users.get(0).getName()));
        // assertTrue(groupingOwner.getComposite().getUsernames().contains(users.get(0).getUsername()));
        assertTrue(groupingOwner.getComposite().getUhUuids().contains(users.get(0).getUhUuid()));
        assertTrue(groupingOwner.getInclude().getNames().contains(users.get(5).getName()));
        assertTrue(groupingOwner.getExclude().getNames().contains(users.get(2).getName()));
        assertTrue(groupingOwner.getBasis().getNames().contains(users.get(4).getName()));
        assertTrue(groupingOwner.getOwners().getNames().contains(users.get(0).getName()));

        assertTrue(groupingAdmin.getComposite().getNames().contains(users.get(0).getName()));
        // assertTrue(groupingAdmin.getComposite().getUsernames().contains(users.get(0).getUsername()));
        assertTrue(groupingAdmin.getComposite().getUhUuids().contains(users.get(0).getUhUuid()));
        assertTrue(groupingAdmin.getInclude().getNames().contains(users.get(5).getName()));
        assertTrue(groupingAdmin.getExclude().getNames().contains(users.get(2).getName()));
        assertTrue(groupingAdmin.getBasis().getNames().contains(users.get(4).getName()));
        assertTrue(groupingAdmin.getOwners().getNames().contains(users.get(0).getName()));
    }

    @Test
    public void getPaginatedGroupingTest() {

        try {
            groupingAssignmentService
                    .getPaginatedGrouping(GROUPING_0_PATH, users.get(1).getUsername(), 1, 4, "name", true);
            fail("Shouldn't be here.");
        } catch (AccessDeniedException ade) {
            assertThat(ade.getMessage(), equalTo(grouperConfiguration.getInsufficientPrivileges()
            ));
        }

        Grouping groupingOwner = groupingAssignmentService
                .getPaginatedGrouping(GROUPING_0_PATH, users.get(0).getUsername(), 1, 4, "name", true);
        Grouping groupingAdmin = groupingAssignmentService
                .getPaginatedGrouping(GROUPING_0_PATH, ADMIN_USER, 1, 4, "name", false);
        Grouping groupingNull = groupingAssignmentService
                .getPaginatedGrouping(GROUPING_0_PATH, users.get(0).getUsername(), null, null, null, null);

        //        assertThat(groupingRandom.getComposite().getMembers().size(), equalTo(0));
        //        assertThat(groupingRandom.getBasis().getMembers().size(), equalTo(0));
        //        assertThat(groupingRandom.getInclude().getMembers().size(), equalTo(0));
        //        assertThat(groupingRandom.getExclude().getMembers().size(), equalTo(0));
        //        assertThat(groupingRandom.getOwners().getMembers().size(), equalTo(0));

        assertTrue(groupingOwner.getComposite().getNames().contains(users.get(0).getName()));
        // assertTrue(groupingOwner.getComposite().getUsernames().contains(users.get(0).getUsername()));
        assertTrue(groupingOwner.getComposite().getUhUuids().contains(users.get(0).getUhUuid()));
        assertFalse(groupingOwner.getComposite().getNames().contains(users.get(7).getName()));
        // assertFalse(groupingOwner.getComposite().getUsernames().contains(users.get(7).getUsername()));
        assertFalse(groupingOwner.getComposite().getUhUuids().contains(users.get(7).getUhUuid()));

        assertTrue(groupingAdmin.getComposite().getNames().contains(users.get(7).getName()));
        // assertTrue(groupingAdmin.getComposite().getUsernames().contains(users.get(7).getUsername()));
        assertTrue(groupingAdmin.getComposite().getUhUuids().contains(users.get(7).getUhUuid()));
        assertFalse(groupingAdmin.getComposite().getNames().contains(users.get(0).getName()));
        //  assertFalse(groupingAdmin.getComposite().getUsernames().contains(users.get(0).getUsername()));
        assertFalse(groupingAdmin.getComposite().getUhUuids().contains(users.get(0).getUhUuid()));

        assertTrue(groupingNull.getComposite().getNames().contains(users.get(0).getName()));
        //assertTrue(groupingNull.getComposite().getUsernames().contains(users.get(0).getUsername()));
        assertTrue(groupingNull.getComposite().getUhUuids().contains(users.get(0).getUhUuid()));
        assertTrue(groupingNull.getComposite().getNames().contains(users.get(7).getName()));
        // assertTrue(groupingNull.getComposite().getUsernames().contains(users.get(7).getUsername()));
        assertTrue(groupingNull.getComposite().getUhUuids().contains(users.get(7).getUhUuid()));
    }

    @Test
    public void getMyGroupingsTest() {
        GroupingAssignment myGroupings = groupingAssignmentService.getGroupingAssignment(users.get(1).getUsername());

        assertThat(myGroupings.getGroupingsOwned().size(), is(0));
        assertThat(myGroupings.getGroupingsIn().size(), is(5));
        assertThat(myGroupings.getGroupingsOptedInTo().size(), is(0));
        assertThat(myGroupings.getGroupingsOptedOutOf().size(), is(0));
        assertThat(myGroupings.getGroupingsToOptInTo().size(), is(0));
        assertThat(myGroupings.getGroupingsToOptOutOf().size(), is(2));

    }

    @Test
    public void groupingsToOptTest() {
        GroupingAssignment myGroupings = groupingAssignmentService.getGroupingAssignment(users.get(1).getUsername());
        //todo finish
    }

    @Test
    public void groupingsInTest() {

        Iterable<Group> groupsIn = groupRepository.findByMembersUsername(users.get(6).getUsername());
        List<String> groupPaths = new ArrayList<>();

        for (Group group : groupsIn) {
            groupPaths.add(group.getPath());
        }
        List<String> supposedGroupings =
                groupPaths.stream().filter(groupPath -> groupPath.matches("[a-zA-Z0-9:]*grouping[0-9]*"))
                        .collect(Collectors.toList());

        List<Grouping> groupingsIn = groupingAssignmentService.groupingsIn(groupPaths);
        List<String> groupingPaths = groupingsIn.stream().map(Grouping::getPath).collect(Collectors.toList());

        for (String path : supposedGroupings) {
            assertTrue(groupingPaths.contains(path));
        }
        for (Grouping grouping : groupingsIn) {
            assertTrue(supposedGroupings.contains(grouping.getPath()));
        }
    }

    @Test
    public void getOptInGroupsTest() {
        List<List<String>> optInPathsLists = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            optInPathsLists.add(new ArrayList<>(
                    groupingAssignmentService.getOptInGroups(users.get(0).getUsername(), users.get(i).getUsername())));
        }
        List<String> assumedPaths = Arrays.asList(GROUPING_0_PATH, GROUPING_1_PATH, GROUPING_3_PATH);

        for (List<String> list : optInPathsLists) {
            for (String path : list) {
                assertTrue(assumedPaths.contains(path));
            }
        }
    }

    @Test
    public void groupingsOwnedTest() {
        Iterable<Group> groupsIn = groupRepository.findByMembersUsername(users.get(0).getUsername());
        List<String> groupPaths = new ArrayList<>();

        for (Group group : groupsIn) {
            groupPaths.add(group.getPath());
        }

        List<Grouping> groupingsOwned = groupingAssignmentService.groupingsOwned(groupPaths);

        for (int i = 0; i < groupingsOwned.size(); i++) {
            assertEquals(groupingsOwned.get(i).getPath(), PATH_ROOT + i);
        }
    }

    @Test
    public void groupingsOptedIntoTest() {
        String user5 = users.get(5).getUsername();

        Iterable<Group> groups = groupRepository.findByMembersUsername(user5);
        List<String> groupPaths = new ArrayList<>();
        for (Group group : groups) {
            groupPaths.add(group.getPath());
        }

        List<Grouping> groupingsOptedInto = groupingAssignmentService.groupingsOptedInto(user5, groupPaths);

        //starts with no groupings opted into
        assertThat(groupingsOptedInto.size(), is(0));

        //opt into a grouping
        membershipService.optIn(user5, GROUPING_1_PATH);
        groupingsOptedInto = groupingAssignmentService.groupingsOptedInto(user5, groupPaths);
        assertThat(groupingsOptedInto.size(), is(1));

        //opt into another grouping
        membershipService.optIn(user5, GROUPING_3_PATH);
        groupingsOptedInto = groupingAssignmentService.groupingsOptedInto(user5, groupPaths);
        assertThat(groupingsOptedInto.size(), is(2));

        //opt out of a grouping
        membershipService.optOut(user5, GROUPING_3_PATH);
        groupingsOptedInto = groupingAssignmentService.groupingsOptedInto(user5, groupPaths);
        assertThat(groupingsOptedInto.size(), is(1));

        //opt out of another grouping
        membershipService.optOut(user5, GROUPING_1_PATH);
        groupingsOptedInto = groupingAssignmentService.groupingsOptedInto(user5, groupPaths);
        assertThat(groupingsOptedInto.size(), is(0));
    }

    @Test
    public void groupingsOptedOutOfTest() {
        String user1 = users.get(1).getUsername();

        Iterable<Group> groups = groupRepository.findByMembersUsername(user1);
        List<String> groupPaths = new ArrayList<>();
        for (Group group : groups) {
            groupPaths.add(group.getPath());
        }

        List<Grouping> groupingsOptedOutOf = groupingAssignmentService.groupingsOptedOutOf(user1, groupPaths);

        //starts with no groupings out of
        assertThat(groupingsOptedOutOf.size(), is(0));

        //opt out of a grouping
        membershipService.optOut(user1, GROUPING_1_PATH);
        groups = groupRepository.findByMembersUsername(user1);
        groupPaths = new ArrayList<>();
        for (Group group : groups) {
            groupPaths.add(group.getPath());
        }
        groupingsOptedOutOf = groupingAssignmentService.groupingsOptedOutOf(user1, groupPaths);
        assertThat(groupingsOptedOutOf.size(), is(1));

        //opt out of another grouping
        membershipService.optOut(user1, GROUPING_3_PATH);
        groups = groupRepository.findByMembersUsername(user1);
        groupPaths = new ArrayList<>();
        for (Group group : groups) {
            groupPaths.add(group.getPath());
        }
        groupingsOptedOutOf = groupingAssignmentService.groupingsOptedOutOf(user1, groupPaths);
        assertThat(groupingsOptedOutOf.size(), is(2));

        //opt into a grouping
        membershipService.optIn(user1, GROUPING_3_PATH);
        groups = groupRepository.findByMembersUsername(user1);
        groupPaths = new ArrayList<>();
        for (Group group : groups) {
            groupPaths.add(group.getPath());
        }
        groupingsOptedOutOf = groupingAssignmentService.groupingsOptedOutOf(user1, groupPaths);
        assertThat(groupingsOptedOutOf.size(), is(1));

        //opt into another grouping
        membershipService.optIn(user1, GROUPING_1_PATH);
        groups = groupRepository.findByMembersUsername(user1);
        groupPaths = new ArrayList<>();
        for (Group group : groups) {
            groupPaths.add(group.getPath());
        }
        groupingsOptedOutOf = groupingAssignmentService.groupingsOptedOutOf(user1, groupPaths);
        assertThat(groupingsOptedOutOf.size(), is(0));
    }

    @Test
    public void adminListsTest() {
        AdminListsHolder adminListsHolder = groupingAssignmentService.adminLists(ADMIN_USER);

        assertThat(5, is(adminListsHolder.getAllGroupings().size()));
        assertThat(1, is(adminListsHolder.getAdminGroup().getMembers().size()));

        try {
            groupingAssignmentService.adminLists(users.get(1).getUsername());
        } catch (AccessDeniedException ade) {
            assertThat(grouperConfiguration.getInsufficientPrivileges()

                    , is(ade.getMessage()));
        }
    }

    /////////////////////////////////////////////////////
    // non-mocked tests//////////////////////////////////
    /////////////////////////////////////////////////////

    @Test
    public void extractGroupPaths() {
        List<String> groupNames = groupingAssignmentService.extractGroupPaths(null);
        assertThat(groupNames.size(), is(0));

        List<WsGroup> groups = new ArrayList<>();
        final int size = 300;

        for (int i = 0; i < size; i++) {
            WsGroup w = new WsGroup();
            w.setName("testName_" + i);
            groups.add(w);
        }
        assertThat(groups.size(), is(size));

        groupNames = groupingAssignmentService.extractGroupPaths(groups);
        for (int i = 0; i < size; i++) {
            assertTrue(groupNames.contains("testName_" + i));
        }
        assertThat(groupNames.size(), is(size));

        // Create some duplicates.
        groups = new ArrayList<>();
        for (int j = 0; j < 3; j++) {
            for (int i = 0; i < size; i++) {
                WsGroup w = new WsGroup();
                w.setName("testName_" + i);
                groups.add(w);
            }
        }
        assertThat(groups.size(), is(size * 3));

        // Duplicates should not be in groupNames list.
        groupNames = groupingAssignmentService.extractGroupPaths(groups);
        assertThat(groupNames.size(), is(size));
        for (int i = 0; i < size; i++) {
            assertTrue(groupNames.contains("testName_" + i));
        }
    }

    @Test
    public void getMembershipAssignmentTest() {
        String username = users.get(0).getUsername();
        //    // username should already be in GROUPING_0_PATH
        //    List<String> groupingsIn = groupingAssignmentService
        //        .getMembershipAssignment(username, username)
        //        .getGroupingsIn()
        //        .stream()
        //        .map(Grouping::getPath)
        //        .collect(Collectors.toList());
        //
        //    List<String> groupingsToOptInto = groupingAssignmentService
        //        .getMembershipAssignment(username, username)
        //        .getGroupingsToOptInTo()
        //        .stream()
        //        .map(Grouping::getPath)
        //        .collect(Collectors.toList());
        //
        //    assertTrue(groupingsIn.contains(GROUPING_0_PATH));
        //    assertFalse(groupingsToOptInto.contains(GROUPING_0_PATH));
        //
        //    // take username[1] out of GROUPING
        //    membershipService.deleteGroupingMember(username, GROUPING_0_PATH, username);
        //
        //    // GROUPING has OPT-IN turned on, so username[1] should be able to opt back into GROUPING
        //    groupingsIn = groupingAssignmentService
        //        .getMembershipAssignment(username, username)
        //        .getGroupingsIn()
        //        .stream()
        //        .map(Grouping::getPath)
        //        .collect(Collectors.toList());
        //
        //    groupingsToOptInto = groupingAssignmentService
        //        .getMembershipAssignment(username, username)
        //        .getGroupingsToOptInTo()
        //        .stream()
        //        .map(Grouping::getPath)
        //        .collect(Collectors.toList());
        //
        //    assertFalse(groupingsIn.contains(GROUPING_0_PATH));
        //    assertTrue(groupingsToOptInto.contains(GROUPING_0_PATH));

        MembershipAssignment membershipAssignment;

        // Test getting the attributes inInclude, inExclude, inOwner, and inBasis.
        membershipAssignment = groupingAssignmentService.getMembershipAssignment(username, username);

        assertTrue(membershipAssignment.isInOwner(GROUPING_0_PATH));
        assertTrue(membershipAssignment.isInBasis(GROUPING_0_PATH));
        assertFalse(membershipAssignment.isInInclude(GROUPING_0_PATH));
        assertFalse(membershipAssignment.isInExclude(GROUPING_0_PATH));

        // Try to get the memberships for a user that doesn't exist.
        try {
            groupingAssignmentService.getMembershipAssignment(username, "somenamethatNoexist");

        } catch (Exception e) {
            System.out.println("User doesn't exist.");
        }
    }

    @Test
    public void makeGroupsTest() {

        WsGetMembersResults getMembersResults = new WsGetMembersResults();
        String[] attributeNames = new String[] { grouperConfiguration.getPersonAttributesUsername(),
                grouperConfiguration.getPersonAttributesUhuuid(),
                grouperConfiguration.getPersonAttributesLastName(),
                grouperConfiguration.getPersonAttributesCompositeName(),
                grouperConfiguration.getPersonAttributesFirstName() };

        // We create an array here because getMembersResults.setResults() only takes an array
        WsGetMembersResult[] getMembersResult = new WsGetMembersResult[1];
        WsGetMembersResult subGetMembersResult = new WsGetMembersResult();

        WsGroup wsGroup = new WsGroup();
        wsGroup.setName(GROUPING_0_PATH);

        WsSubject[] list = new WsSubject[3];
        for (int i = 0; i < 3; i++) {
            list[i] = new WsSubject();
            list[i].setName("testSubject_" + i);
            list[i].setId("testSubject_uuid_" + i);
            // Attribute values need to match names in order (uuid is set seperately, so it can be blank here
            list[i].setAttributeValues(new String[] { "testSubject_username_" + i, "", "", "testSubject_" + i, "" });
        }

        subGetMembersResult.setWsSubjects(list);
        subGetMembersResult.setWsGroup(wsGroup);
        getMembersResult[0] = subGetMembersResult;

        getMembersResults.setResults(getMembersResult);
        getMembersResults.setSubjectAttributeNames(attributeNames);

        Map<String, Group> groups = groupingAssignmentService.makeGroups(getMembersResults);

        assertThat(groups.size(), not(equalTo(0)));
        Group resultGroup = groups.get(GROUPING_0_PATH);

        for (int i = 0; i < resultGroup.getMembers().size(); i++) {
            assertEquals(resultGroup.getMembers().get(i).getName(), "testSubject_" + i);
            assertTrue(resultGroup.getNames().contains("testSubject_" + i));
            assertEquals(resultGroup.getMembers().get(i).getUhUuid(), "testSubject_uuid_" + i);
            assertTrue(resultGroup.getUhUuids().contains("testSubject_uuid_" + i));
            assertEquals(resultGroup.getMembers().get(i).getUsername(), "testSubject_username_" + i);
            assertTrue(resultGroup.getUsernames().contains("testSubject_username_" + i));
        }
    }

    @Test
    public void makePersonTest() {
        String name = "name";
        String id = "uuid";
        String identifier = "username";
        String[] attributeNames = new String[] { grouperConfiguration.getPersonAttributesUsername(),
                grouperConfiguration.getPersonAttributesUhuuid(),
                grouperConfiguration.getPersonAttributesLastName(),
                grouperConfiguration.getPersonAttributesCompositeName(),
                grouperConfiguration.getPersonAttributesFirstName() };
        String[] attributeValues = new String[] { identifier, id, null, name, null };

        WsSubject subject = new WsSubject();
        subject.setName(name);
        subject.setId(id);
        subject.setAttributeValues(attributeValues);

        Person person = groupingAssignmentService.makePerson(subject, attributeNames);

        assertEquals(person.getName(), name);
        assertEquals(person.getUhUuid(), id);
        assertEquals(person.getUsername(), identifier);

        assertNotNull(groupingAssignmentService.makePerson(new WsSubject(), new String[] {}));
    }

    //todo Finish this test for setGroupingAttributes
    @Test
    public void setGroupingAttributesTest() {
        groupingAssignmentService.getGrouping(GROUPING_3_PATH, users.get(0).getUsername());
    }

    @Test
    public void makeGroupsNullValuesTest() {

        WsGetMembersResults getMembersResults = new WsGetMembersResults();
        String[] attributeNames = new String[] { grouperConfiguration.getPersonAttributesUsername(),
                grouperConfiguration.getPersonAttributesUhuuid(),
                grouperConfiguration.getPersonAttributesLastName(),
                grouperConfiguration.getPersonAttributesCompositeName(),
                grouperConfiguration.getPersonAttributesFirstName() };

        // We create an array here because getMembersResults.setResults() only takes an array
        // nullSubject... will have a null Subject Array while testSubject... will have 2 subjects with test attributes
        WsGetMembersResult[] getMembersResult = new WsGetMembersResult[2];
        WsGetMembersResult nullSubjectArrayGetMembersResult = new WsGetMembersResult();
        WsGetMembersResult testSubjectsGetMembersResult = new WsGetMembersResult();

        WsGroup wsGroup0 = new WsGroup();
        wsGroup0.setName(GROUPING_0_PATH);
        nullSubjectArrayGetMembersResult.setWsGroup(wsGroup0);

        WsGroup wsGroup1 = new WsGroup();
        wsGroup1.setName(GROUPING_3_BASIS_PATH);
        testSubjectsGetMembersResult.setWsGroup(wsGroup1);

        // Set up subjects: one null, one with grouperConfiguration.getStaleSubjectId()
        WsSubject[] list = new WsSubject[2];
        list[1] = new WsSubject();
        list[1].setName("iDontExistAnymoreName");
        list[1].setId("iDontExistAnymoreUHUUID");
        list[1].setAttributeValues(new String[] { "iDontExistAnymoreUsername", "", "", "iDontExistAnymoreName", "" });
        list[1].setSourceId(grouperConfiguration.getStaleSubjectId());

        // Push to array and set results
        nullSubjectArrayGetMembersResult.setWsSubjects(null);
        testSubjectsGetMembersResult.setWsSubjects(list);
        getMembersResult[0] = nullSubjectArrayGetMembersResult;
        getMembersResult[1] = testSubjectsGetMembersResult;
        getMembersResults.setResults(getMembersResult);
        getMembersResults.setSubjectAttributeNames(attributeNames);

        Map<String, Group> groups = groupingAssignmentService.makeGroups(getMembersResults);

        // Only one result should've created a group (the non-null subject array one)
        // The second group should have only one member because it had a null subject
        assertThat(groups.size(), equalTo(1));
        assertThat(groups.get(GROUPING_3_BASIS_PATH).getMembers().size(), equalTo(1));

        // Second group
        // Check if stale subject id produces the right username restructure
        List<Person> group1Members = groups.get(GROUPING_3_BASIS_PATH).getMembers();
        assertThat(group1Members.get(0).getUsername(), equalTo("User Not Available."));
    }
}