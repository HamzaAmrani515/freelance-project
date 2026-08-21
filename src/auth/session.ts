import type { DemoUser } from "./demoUsers";

const SESSION_KEY = "freelance_demo_current_user";

export function setCurrentUser(user: DemoUser) {
  localStorage.setItem(SESSION_KEY, JSON.stringify(user));
}

export function getCurrentUser(): DemoUser | null {
  const raw = localStorage.getItem(SESSION_KEY);

  if (!raw) return null;

  try {
    return JSON.parse(raw) as DemoUser;
  } catch {
    localStorage.removeItem(SESSION_KEY);
    return null;
  }
}

export function clearCurrentUser() {
  localStorage.removeItem(SESSION_KEY);
}