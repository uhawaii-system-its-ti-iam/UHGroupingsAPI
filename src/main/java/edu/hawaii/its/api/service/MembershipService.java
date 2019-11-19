package edu.hawaii.its.api.service;

import edu.hawaii.its.api.type.GroupingsServiceResult;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.List;

public interface MembershipService {

    public List<GroupingsServiceResult> addGroupingMember(String ownerUsername, String groupingPath, String userToAdd);

    public List<GroupingsServiceResult> addGroupingMemberByUsername(String ownerUsername, String groupingPath,
            String userToAddUsername);

    public List<GroupingsServiceResult> addGroupingMemberByUuid(String ownerUsername, String groupingPath,
            String userToAddUuid);

    public List<GroupingsServiceResult> addGroupMember(String ownerUsername, String groupingPath, String userToAdd);

    public List<GroupingsServiceResult> addGroupMemberByUsername(String ownerUsername, String groupPath,
            String userToAddUsername);

    public List<GroupingsServiceResult> addGroupMemberByUuid(String ownerUsername, String groupPath,
            String userToAddUsername);

    public List<List<GroupingsServiceResult>> addGroupMembers(String ownerUsername, String groupingPath, List<String> usersToAdd) throws MessagingException, IOException;

    public List<GroupingsServiceResult> addGroupMembersByUsername(String ownerUsername, String group,
            List<String> usersToAddUsername);

    public List<GroupingsServiceResult> addGroupMembersByUuid(String ownerUsername, String group,
            List<String> usersToAddUuid);

    public List<GroupingsServiceResult> deleteGroupingMemberByUsername(String ownerUsername, String groupingPath,
            String userToDeleteUsername);

    public List<GroupingsServiceResult> deleteGroupingMemberByUuid(String ownerUsername, String groupingPath,
            String userToDeleteUuid);

    public GroupingsServiceResult deleteGroupMemberByUsername(String ownerUsername, String groupPath,
            String userToDeleteUsername);

    public GroupingsServiceResult deleteGroupMemberByUuid(String ownerUsername, String groupPath,
            String userToDeleteUuid);

    //todo deleteGroupMembersByUuid
    //todo deleteGroupMembersByUsername

    public GroupingsServiceResult deleteGroupMember(String ownerUsername, String groupPath,
            String userToDelete);

    public List<String> listOwned(String adminUsername, String username);

    public GroupingsServiceResult addAdmin(String adminUsername, String adminToAddUsername);

    public GroupingsServiceResult deleteAdmin(String adminUsername, String adminToDeleteUsername);

    public List<GroupingsServiceResult> optIn(String username, String groupingPath);

    public List<GroupingsServiceResult> optOut(String username, String groupingPath);

    public List<GroupingsServiceResult> optIn(String username, String groupingPath, String uid);

    public List<GroupingsServiceResult> optOut(String username, String groupingPath, String uid);

    public boolean isGroupCanOptIn(String username, String groupPath);

    public boolean isGroupCanOptOut(String username, String groupPath);

    //do not include in REST controller
    public GroupingsServiceResult updateLastModified(String groupPath);

    public GroupingsServiceResult addSelfOpted(String groupPath, String username);

    public GroupingsServiceResult removeSelfOpted(String groupPath, String username);
}
