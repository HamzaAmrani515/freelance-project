export async function http<T>(
  url: string,
  options: RequestInit = {}
): Promise<T> {
  const base = (process.env.REACT_APP_API_URL ?? "http://localhost:8080").trim();

  const response = await fetch(`${base}${url}`, {
    method: options.method ?? "GET",
    headers: {
      "Content-Type": "application/json",
      ...(options.headers || {}),
    },
    body: options.body,
  });

  if (!response.ok) {
    const text = await response.text();
    throw new Error(text || `HTTP ${response.status}`);
  }

  if (response.status === 204) {
    return undefined as T;
  }

  return response.json() as Promise<T>;
}