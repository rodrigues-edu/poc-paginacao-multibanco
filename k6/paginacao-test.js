import http from 'k6/http';
import { sleep, check } from 'k6';

export const options = {
    stages: [
        { duration: '10s', target: 10 }, // ramp-up
        { duration: '30s', target: 10 }, // carga constante
        { duration: '10s', target: 0 }   // ramp-down
    ],
    thresholds: {
        http_req_duration: ['p(95)<500'], // 95% das requisições abaixo de 500ms
        http_req_failed: ['rate<0.01'],   // menos de 1% de falhas
    },
};

const BASE_URL = 'http://localhost:8080';

export default function () {
    const endpoints = [
        `${BASE_URL}/paginacao/offset?page=1&size=10`,
        `${BASE_URL}/paginacao/cursor?lastId=1000&limit=10`,
        `${BASE_URL}/paginacao/time?from=2024-01-01&to=2024-12-31&limit=10`
    ];

    endpoints.forEach(url => {
        const res = http.get(url);
        check(res, {
            'status é 200': (r) => r.status === 200,
            'resposta abaixo de 500ms': (r) => r.timings.duration < 500,
        });
        sleep(1);
    });
}
