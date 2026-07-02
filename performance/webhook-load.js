import http from "k6/http";
import { check } from "k6";

export const options = {
    vus: 20,
    duration: "30s",

    thresholds: {
        http_req_duration: ["p(95)<500"],
        http_req_failed: ["rate<0.01"],
    },
};

const BASE_URL = "http://localhost:8080";

const orderIds = [
    "order_T8k1M79OD1HkVA",
    "order_T8k1MI4LK5VTeu",
    "order_T8k1QKbkWDIUFj",
    "order_T8k1Rg8cLSYphP",
    "order_T8k1RpRYZaYGMg",
    "order_T8k1Wxz2xzjLyK",
    "order_T8k1b70v73oiY5",
    "order_T8k1cSUSpxrCwE",
    "order_T8k1drOaoENE27",
    "order_T8k1fJ2XPAaenZ",
    "order_T8k1gjH6rmCrdr",
    "order_T8k1jYMqaQ9RbS",
    "order_T8k1M7oWalX5Iu",
    "order_T8k1QKh8xGGT31",
    "order_T8k1QQWHNeFSl4",
    "order_T8k1Ri2QRuyZih",
    "order_T8k1TCcG9Q7yrI",
    "order_T8k1XytL33ad3y",
    "order_T8k1YFhprwDhoU",
    "order_T8k1ZcxhHZIemv",
    "order_T8k1b23AFrJEdx",
    "order_T8k1cTlgLvBrce",
    "order_T8k1fJYeWERpEk",
    "order_T8k1fQmd3gR3b3",
    "order_T8k1gkDMbG6DQu",
    "order_T8k1jYPr2KcQZv"
];

export default function () {

    const orderId =
        orderIds[Math.floor(Math.random() * orderIds.length)];

    const payload = JSON.stringify({
        event: "payment.captured",
        payload: {
            payment: {
                entity: {
                    order_id: orderId
                }
            }
        }
    });

    const res = http.post(
        `${BASE_URL}/api/v1/webhooks/razorpay`,
        payload,
        {
            headers: {
                "Content-Type": "application/json",
                "X-Razorpay-Signature": "dummy"
            }
        }
    );

    check(res, {
        "status is 200": (r) => r.status === 200
    });
}