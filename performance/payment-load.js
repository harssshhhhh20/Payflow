import http from "k6/http";
import { check, sleep } from "k6";

export const options = {
  vus: 20,
  duration: "30s",
  thresholds: {
    http_req_duration: ["p(95)<1000"],

    http_req_failed: ["rate<0.01"],
  },
};

const BASE_URL = "http://localhost:8080";

const API_KEY = "pf_live_Chu5ovRR4dFMLdjbkadeELXY3TtmeXyC";

export default function () {
  const payload = JSON.stringify({
    amount: 50,

    currency: "INR",

    idempotencyKey: `${__VU}-${__ITER}-${Date.now()}`,
  });

  const params = {
    headers: {
      "Content-Type": "application/json",

      "X-API-KEY": API_KEY,
    },
  };

  const res = http.post(`${BASE_URL}/api/v1/payments`, payload, params);

  if (res.status != 200 && res.status != 201) {
    console.log("STATUS:", res.status);
    console.log(res.body);
  }

  check(res, {
    "status is 200 or 201": (r) => r.status === 200 || r.status === 201,
  });

  sleep(1);
}
