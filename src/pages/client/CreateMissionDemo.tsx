import React, { useState } from "react";
import { http } from "../../api/http";

export default function CreateMissionDemo() {
  const [missionId, setMissionId] = useState<number>(1);
  const [result, setResult] = useState<string>("");

  const dispatch = async () => {
    setResult("");
    const res = await http<string>(`/api/notifications/v2/mission/${missionId}/dispatch`, {
      method: "POST",
    });
    setResult(res);
  };

  return (
    <div className="bg-white border rounded-xl p-6 space-y-4">
      <h1 className="text-xl font-semibold">Démo Dispatch Notifications</h1>

      <div className="flex items-center gap-2">
        <input
          className="border rounded-lg px-3 py-2 w-40"
          type="number"
          value={missionId}
          onChange={(e) => setMissionId(Number(e.target.value))}
        />
        <button className="px-3 py-2 rounded-lg border hover:bg-gray-50" onClick={dispatch}>
          Dispatch
        </button>
      </div>

      {result && <p className="text-sm">{result}</p>}
    </div>
  );
}