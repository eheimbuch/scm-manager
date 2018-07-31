import { apiClient } from "../../apiclient";
import { isPending } from "../../modules/pending";
import { getFailure } from "../../modules/failure";
import * as types from "../../modules/types";
import { combineReducers, Dispatch } from "redux";
import type { Action } from "../../types/Action";
import type { PagedCollection } from "../../types/Collection";

export const FETCH_GROUPS = "scm/groups/FETCH_GROUPS";
export const FETCH_GROUPS_PENDING = `${FETCH_GROUPS}_${types.PENDING_SUFFIX}`;
export const FETCH_GROUPS_SUCCESS = `${FETCH_GROUPS}_${types.SUCCESS_SUFFIX}`;
export const FETCH_GROUPS_FAILURE = `${FETCH_GROUPS}_${types.FAILURE_SUFFIX}`;

export const FETCH_GROUP = "scm/groups/FETCH_GROUP";
export const FETCH_GROUP_PENDING = `${FETCH_GROUP}_${types.PENDING_SUFFIX}`;
export const FETCH_GROUP_SUCCESS = `${FETCH_GROUP}_${types.SUCCESS_SUFFIX}`;
export const FETCH_GROUP_FAILURE = `${FETCH_GROUP}_${types.FAILURE_SUFFIX}`;

export const CREATE_GROUP = "scm/groups/CREATE_GROUP";
export const CREATE_GROUP_PENDING = `${CREATE_GROUP}_${types.PENDING_SUFFIX}`;
export const CREATE_GROUP_SUCCESS = `${CREATE_GROUP}_${types.SUCCESS_SUFFIX}`;
export const CREATE_GROUP_FAILURE = `${CREATE_GROUP}_${types.FAILURE_SUFFIX}`;
export const CREATE_GROUP_RESET = `${CREATE_GROUP}_${types.RESET_SUFFIX}`;

export const MODIFY_GROUP = "scm/groups/MODIFY_GROUP";
export const MODIFY_GROUP_PENDING = `${MODIFY_GROUP}_${types.PENDING_SUFFIX}`;
export const MODIFY_GROUP_SUCCESS = `${MODIFY_GROUP}_${types.SUCCESS_SUFFIX}`;
export const MODIFY_GROUP_FAILURE = `${MODIFY_GROUP}_${types.FAILURE_SUFFIX}`;

export const DELETE_GROUP = "scm/groups/DELETE";
export const DELETE_GROUP_PENDING = `${DELETE_GROUP}_${types.PENDING_SUFFIX}`;
export const DELETE_GROUP_SUCCESS = `${DELETE_GROUP}_${types.SUCCESS_SUFFIX}`;
export const DELETE_GROUP_FAILURE = `${DELETE_GROUP}_${types.FAILURE_SUFFIX}`;

const GROUPS_URL = "groups";

// fetch groups

export function fetchGroups() {
  return fetchGroupsByLink(GROUPS_URL);
}

export function fetchGroupsByPage(page: number) {
  // backend start counting by 0
  return fetchGroupsByLink(GROUPS_URL + "?page=" + (page - 1));
}

export function fetchGroupsByLink(link: string) {
  return function(dispatch: any) {
    dispatch(fetchGroupsPending());
    return apiClient
      .get(link)
      .then(response => response.json())
      .then(data => {
        dispatch(fetchGroupsSuccess(data));
      })
      .catch(cause => {
        const error = new Error(`could not fetch groups: ${cause.message}`);
        dispatch(fetchGroupsFailure(GROUPS_URL, error));
      });
  };
}

export function fetchGroupsPending(): Action {
  return {
    type: FETCH_GROUPS_PENDING
  };
}

export function fetchGroupsSuccess(groups: any): Action {
  return {
    type: FETCH_GROUPS_SUCCESS,
    payload: groups
  };
}

export function fetchGroupsFailure(url: string, error: Error): Action {
  return {
    type: FETCH_GROUPS_FAILURE,
    payload: {
      error,
      url
    }
  };
}


//reducer
function extractGroupsByNames(
  groups: Groups[],
  groupNames: string[],
  oldGroupsByNames: Object
) {
  const groupsByNames = {};

  for (let group of groups) {
    groupsByNames[group.name] = group;
  }

  for (let groupName in oldGroupsByNames) {
    groupsByNames[groupName] = oldGroupsByNames[groupName];
  }
  return groupsByNames;
}


const reducerByName = (state: any, groupname: string, newGroupState: any) => {
  const newGroupsByNames = {
    ...state,
    [groupname]: newGroupState
  };

  return newGroupsByNames;
};

function listReducer(state: any = {}, action: any = {}) {
  switch (action.type) {
    case FETCH_GROUPS_SUCCESS:
      const groups = action.payload._embedded.groups;
      const groupNames = groups.map(group => group.name);
      return {
        ...state,
        entries: groupNames,
        entry: {
          groupCreatePermission: action.payload._links.create ? true : false,
          page: action.payload.page,
          pageTotal: action.payload.pageTotal,
          _links: action.payload._links
        }
      };

    default:
      return state;
  }
}

function byNamesReducer(state: any = {}, action: any = {}) {
  switch (action.type) {
    // Fetch all groups actions
    case FETCH_GROUPS_SUCCESS:
      const groups = action.payload._embedded.groups;
      const groupNames = groups.map(group => group.name);
      const byNames = extractGroupsByNames(groups, groupNames, state.byNames);
      return {
        ...byNames
      };

    default:
      return state;
  }
}

export default combineReducers({
  list: listReducer,
  byNames: byNamesReducer
});
