import type {ReactNode} from "react";
import {createContext, useEffect, useContext, useReducer} from "react";

type User = {
  id?: string;
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
  jwtToken: null,
  user: null,
  isAuthenticated: null,
  setUser: () => {
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

export const AuthProvider = ({children}: { children: ReactNode }) => {
  const initialAuthState = (() => {
    try {
      const jwtToken = localStorage.getItem("jwtToken");
      const user = localStorage.getItem("user");

      if (jwtToken && user) {
        return {
          jwtToken,
          user: JSON.parse(user),
          isAuthenticated: true,
        }
      }
    } catch (error) {
      console.error("Failed to load from localStorage", error);
    }
    return {
      jwtToken: null,
      user: null,
      isAuthenticated: false,
    }
  })();

  const [authState, dispatch] = useReducer(authReducer, initialAuthState);

  useEffect(() => {
    try {
      if (authState.isAuthenticated) {
        localStorage.setItem("jwtToken", authState.jwtToken);
        localStorage.setItem("user", JSON.stringify(authState.user));
      } else {
        localStorage.removeItem("jwtToken");
        localStorage.removeItem("user");
      }
    } catch (error) {
      console.error("Failed to save to localStorage", error);
    }
  }, [authState]);

  const loginSuccess = (jwtToken: string, user: User) => {
    dispatch({type: LOGIN_SUCCESS, payload: {jwtToken, user}});
  };

  const logout = () => {
    dispatch({type: LOGOUT});
  };

  return (
    <AuthContext
      value={{
        jwtToken: authState.jwtToken,
        user: authState.user,
        isAuthenticated: authState.isAuthenticated,
        setUser: () => {
        },
      }}
    >
      {children}
    </AuthContext>

  );
};