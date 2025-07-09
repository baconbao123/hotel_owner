import { Button } from "primereact/button";
import type { Route } from "../../+types/root";
import { useEffect, useState } from "react";
import { Chart } from "primereact/chart";

export function meta({}: Route.MetaArgs) {
    return [
        { title: "Dashboard" },
        { name: "description", content: "Hotel Admin Dashboard" },
    ];
}

export default function Dashboard() {
    const hotelNames = [
  "Sunrise Hotel",
  "Ocean View",
  "Mountain Retreat",
  "City Center Inn",
  "Palm Resort",
  "Skyline Suites",
  "Royal Palace",
  "Green Valley",
  "Nhà của chúng ta",

];

function getRandomInt(min: number, max: number) {
  return Math.floor(Math.random() * (max - min + 1)) + min;
}

function getRandomColor(opacity = 1) {
  const r = getRandomInt(0, 255);
  const g = getRandomInt(0, 255);
  const b = getRandomInt(0, 255);
  return `rgba(${r},${g},${b},${opacity})`;
}

const chartData = [
  {
    data: {
      labels: hotelNames,
      datasets: [
        {
          label: "Bookings",
          data: hotelNames.map(() => getRandomInt(100, 1000)),
          backgroundColor: hotelNames.map(() => getRandomColor(0.5)),
          borderWidth: 2,
        },
      ],
    },
    options: {
      scales: {
        y: {
          beginAtZero: true,
        },
      },
    },
  },
  {
    data: { 
      labels: hotelNames,
      datasets: [
        {
          label: "People",
          data: hotelNames.map(() => getRandomInt(100, 1000)),
          backgroundColor: hotelNames.map(() => getRandomColor(0.5)),
          borderWidth: 2,
        },
      ],
    },
    options: {
      scales: {
        y: {
          beginAtZero: true,
        },
      },
    },
  },
];

const category = [
    {
        title: "Room Types",
        description:"Chart room types",
        data: {
            labels: ["Love room", "Twin room", "Two room"],
            datasets: [
                {
                    data: [
                        getRandomInt(5, 20), // Admin
                        getRandomInt(10, 40), // Owner
                        getRandomInt(50, 200), // Customer
                    ],
                    backgroundColor: [
                        "rgba(59,130,246,0.7)",   // blue-500
                        "rgba(253,224,71,0.7)",   // yellow-500
                        "rgba(34,197,94,0.7)",    // green-500
                    ],
                    hoverBackgroundColor: [
                        "rgba(96,165,250,0.7)",   // blue-400
                        "rgba(254,240,138,0.7)",  // yellow-400
                        "rgba(74,222,128,0.7)",   // green-400
                    ],
                },
            ],
        },
        option: {
            cutout: "60%",
        },
    },
    {
        title: "Hotel Types",
        description: "Chart hotel types",
        data: {
            labels: ["Resort", "Boutique", "Business", "Hostel", "Apartment"],
            datasets: [
                {
                    data: Array.from({ length: 5 }, () => getRandomInt(5, 50)),
                    backgroundColor: [
                        "rgba(244,63,94,0.7)",    // red-500
                        "rgba(59,130,246,0.7)",   // blue-500
                        "rgba(253,224,71,0.7)",   // yellow-500
                        "rgba(34,197,94,0.7)",    // green-500
                        "rgba(168,85,247,0.7)",   // purple-500
                    ],
                    hoverBackgroundColor: [
                        "rgba(248,113,113,0.7)",  // red-400
                        "rgba(96,165,250,0.7)",   // blue-400
                        "rgba(254,240,138,0.7)",  // yellow-400
                        "rgba(74,222,128,0.7)",   // green-400
                        "rgba(192,132,252,0.7)",  // purple-400
                    ],
                },
            ],
        },
        option: {
            cutout: "60%",
        },
    },
    {
        title: "Booking Types",
        description: "Chart booking types",
        data: {
            labels: ["Online", "Walk-in", "Agency", "Corporate", "Other"],
            datasets: [
                {
                    data: Array.from({ length: 5 }, () => getRandomInt(20, 150)),
                    backgroundColor: [
                        "rgba(16,185,129,0.7)",   // emerald-500
                        "rgba(251,191,36,0.7)",   // amber-500
                        "rgba(59,130,246,0.7)",   // blue-500
                        "rgba(239,68,68,0.7)",    // red-500
                        "rgba(168,85,247,0.7)",   // purple-500
                    ],
                    hoverBackgroundColor: [
                        "rgba(52,211,153,0.7)",   // emerald-400
                        "rgba(253,224,71,0.7)",   // amber-400
                        "rgba(96,165,250,0.7)",   // blue-400
                        "rgba(248,113,113,0.7)",  // red-400
                        "rgba(192,132,252,0.7)",  // purple-400
                    ],
                },
            ],
        },
        option: {
            cutout: "60%",
        },
    },
    {
        title: "Feedback",
        description: "Chart feedback   typesi",
        data: {
            labels: ["Positive", "Neutral", "Negative"],
            datasets: [
                {
                    data: [
                        getRandomInt(50, 200), // Positive
                        getRandomInt(10, 50),  // Neutral
                        getRandomInt(5, 30),   // Negative
                    ],
                    backgroundColor: [
                        "rgba(34,197,94,0.7)",    // green-500
                        "rgba(251,191,36,0.7)",   // amber-500
                        "rgba(239,68,68,0.7)",    // red-500
                    ],
                    hoverBackgroundColor: [
                        "rgba(74,222,128,0.7)",   // green-400
                        "rgba(253,224,71,0.7)",   // amber-400
                        "rgba(248,113,113,0.7)",  // red-400
                    ],
                },
            ],
        },
        option: {
            cutout: "60%",
        },
    },
];

    return (
        <div className="">
            <div className="grid grid-cols-4 gap-4 p-4 mb-5">
                {category.map((item: any) => {
                    return (
                       <div
                  key={item.title}
                  className="p-4 border rounded-lg shadow hover:shadow-lg transition-shadow duration-300 bg-white"
              >
                  <h2 className="text-xl font-semibold">
                      {item.title}{" "}
                      <span className="text-base text-gray-500 font-normal">
                          (Total {item.data.datasets[0].data.reduce((a: number, b: number) => a + b, 0)})
                      </span>
                  </h2>
                  <p className="text-gray-600">{item.description}</p>
                  <div className="flex justify-center mt-4">
                      <Chart
                          type="doughnut"
                          data={item.data}
                          options={item.option}
                          className="w-80"
                      />
                  </div>
              </div>
                    );
                })}
            </div>

            <div className="grid grid-cols-2 gap-4 p-4">
              {chartData.map((item: any) => (

                <div className="h-150 bg-white rounded-lg shadow hover:shadow-lg transition-shadow">
                    <Chart type="bar" data={item.data} options={item.options} />
                </div>
              ))}
            </div>
        </div>
    );
}
