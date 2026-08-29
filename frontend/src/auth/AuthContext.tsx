import type {ReactNode} from "react";
import {createContext, useContext, useReducer} from "react";
import {setAuthToken} from "../api/client.ts";

type User = {
  username?: string;
};

type AuthState = {
  jwtToken: string | null;
  user: User | null;
  isAuthenticated: boolean;
};

type AuthAction =
  | { type: "LOGIN_SUCCESS"; payload: { jwtToken: string; user: User } }
  | { type: "LOGOUT" };

export const AuthContext = createContext({
  jwtToken: null as string | null,
  user: null as User | null,
  isAuthenticated: null as boolean | null,
  loginSuccess: (_jwtToken: string, _user: User) => {
  },
  logout: () => {
  },
});

export const useAuth = () => useContext(AuthContext);

// dispatch actions
const LOGIN_SUCCESS = "LOGIN_SUCCESS";
const LOGOUT = "LOGOUT";

const authReducer = (prevState: AuthState, action: AuthAction) => {
  switch (action.type) {
    case LOGIN_SUCCESS:
      return {
        ...prevState,
        jwtToken: action.payload.jwtToken,
        user: action.payload.user,
        isAuthenticated: true,
      }
    case LOGOUT:
      return {
        ...prevState,
        jwtToken: null,
        user: null,
        isAuthenticated: false,
      }
    default:
      return prevState;
  }
}

const initialAuthState: AuthState = ({
  jwtToken: null,
  user: null,
  isAuthenticated: false,
});

export const AuthProvider = ({children}: { children: ReactNode }) => {
  const [authState, dispatch] = useReducer(authReducer, initialAuthState);

  const loginSuccess = (jwtToken: string, user: User) => {
    setAuthToken(jwtToken);
    dispatch({type: LOGIN_SUCCESS, payload: {jwtToken, user}});
  };

  const logout = () => {
    setAuthToken(null);
    dispatch({type: LOGOUT});
  };

  return (
    <AuthContext
      value={{
        jwtToken: authState.jwtToken,
        user: authState.user,
        isAuthenticated: authState.isAuthenticated,
        loginSuccess,
        logout,
      }}
    >
      {children}
    </AuthContext>

  );
};