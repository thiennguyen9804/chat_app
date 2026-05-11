import ws from 'k6/ws';
import { check } from 'k6';
import { Trend, Counter } from 'k6/metrics';
import { SharedArray } from 'k6/data';
import { vu } from 'k6/execution';
import exec from 'k6/execution';

// 2 metric riêng biệt
const latencyWithoutNoise = new Trend('latency_without_noise'); // 2 người, không có noise
const latencyWithNoise = new Trend('latency_with_noise'); // 2 người, không có noise

const usersData = new SharedArray('users_list', function () {
    return JSON.parse(open('../users.json'));
});

const SENDER_USER   = usersData[0];
const RECEIVER_USER = usersData[1];
const CHAT_HUB_URL  = 'ws://localhost:8081/chat-socket';

export const options = {
    scenarios: {
        main_chat: {
            executor: 'constant-vus',
            vus: 2,
            duration: '13s',
            exec: 'mainChatLogic'
        },

        noise_chat: {
            executor: 'ramping-vus',
            startVUs: 0,
            exec: 'noiseLogic',
            startTime: '3s',
            stages: [
                { duration: '3s', target: 2 }, // 20s tăng dần
                { duration: '3s', target: 2 }, // 20s duy trì
                { duration: '4s', target: 0 }
            ]
        },
    }
}

export function mainChatLogic() {
    const TEST_DURATION = 9 * 1000;
    const isSender = __VU === 1;
    const userData = isSender ? SENDER_USER : RECEIVER_USER;
    const wsUrl = `${CHAT_HUB_URL}?access_token=${userData.token}`;
    
    let intervalId;
    const res = ws.connect(wsUrl, null, function (socket) {
        socket.on('open', function () {
            socket.send('{"protocol":"json","version":1}\u001e');
            if (isSender) {
                intervalId = socket.setInterval(function () {
                    const msg = {
                        type: 1,
                        target: "SendMessage",
                        arguments: [SENDER_USER.id, RECEIVER_USER.id, `LATENCY_TEST|${Date.now()}`]
                    };
                    socket.send(JSON.stringify(msg) + '\u001e');
                }, 300);
            }
        });

        socket.on('close', function () {
            if (intervalId) {
                socket.clearInterval(intervalId); // 4. Xóa interval tại đây
                console.log('Đã xóa setInterval và đóng kết nối.');
            }
        });

        socket.on('message', function (data) {
            if (!isSender && data.includes('LATENCY_TEST')) {
                const match = data.match(/LATENCY_TEST\|(\d+)/);
                if (match) {
                    const latency = Date.now() - parseInt(match[1]);
                    const elapsed = exec.instance.currentTestRunDuration;
                    if(elapsed <= 20000) {
                        latencyWithoutNoise.add(latency)
                    } else if(elapsed >= 40000 && elapsed < 60000) {
                        latencyWithNoise.add(latency)
                    }
                }
            }
        });

        const elapsed = exec.instance.currentTestRunDuration
        const remainingTime = Math.max(TEST_DURATION - elapsed, 1000)
        socket.setTimeout(function () {
            if (intervalId) socket.clearInterval(intervalId);
            socket.close();
        }, remainingTime); 
    });
    check(res, { 'Measure user connected': (r) => r && r.status === 101 });
}

export function noiseLogic() {
    const TEST_DURATION = 13 * 1000
    const noiseUser = usersData[__VU - 1]; 
    const wsUrl = `${CHAT_HUB_URL}?access_token=${noiseUser.token}`;

    const res = ws.connect(wsUrl, null, function (socket) {
        socket.on('open', function () {
            socket.send('{"protocol":"json","version":1}\u001e');

            socket.setInterval(function () {
                const msg = {
                    type: 1,
                    target: "SendMessage",
                    arguments: [
                        noiseUser.id, // Sender
                        noiseUser.id, // Receiver (Tự gửi cho chính mình)
                        `NOISE_SELF_SEND|${Math.random().toString(36).substring(7)}`
                    ]
                };
                socket.send(JSON.stringify(msg) + '\u001e');
            }, 500); // Gửi mỗi 500ms
        });

        // Đảm bảo đóng socket sau khi hết thời gian test
        const elapsed = exec.instance.currentTestRunDuration;
        const remainingTime = Math.max(TEST_DURATION - elapsed, 1)
        socket.setTimeout(() => socket.close(), remainingTime);
    });

    check(res, { 'Noise user connected': (r) => r && r.status === 101 });
}

export default function main() {
    console.log(`[Main]`);
}
