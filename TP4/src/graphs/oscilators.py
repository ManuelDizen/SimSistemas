import numpy as np
import matplotlib.pyplot as plt
import math

amp = 1.0
mass = 70.0
k = 10000
gamma = 100.0

MSE_beeman = np.zeros(4)
idx = 0
MSE_verlet = np.zeros(4)
MSE_gear = np.zeros(4)

def actual_val(t):
    return amp * (np.exp(-(gamma / (2 * mass)) * t)) * (
            np.cos(np.power((k / mass) - (gamma * gamma / (4 * (mass * mass))), 0.5) * t))


def calc_mse(calculated, actual, n):
    sum = 0
    for i in range(0, len(calculated)):
        sum = sum + math.pow(calculated[i] - actual[i], 2)
    return sum/n


def parse_file(file):
    readable = file.read().split('\n')[0:]
    i = 0
    for line in readable:
        readable[i] = line.split(' ')[0]
        i = i + 1
    readable.remove('')
    values = np.array(readable).astype(float)
    return values


def scatter(beeman_f, verlet_f, gear_f, step, end):
    beeman = parse_file(beeman_f)
    verlet = parse_file(verlet_f)
    gear = parse_file(gear_f)

    actual = np.zeros(len(beeman))
    times = np.arange(0, end, step)

    for i in range(0, len(beeman)):
        actual[i] = actual_val(times[i])

    diffBeeman = calc_mse(beeman, actual, len(beeman))
    MSE_beeman[idx] = diffBeeman
    diffVerlet = calc_mse(verlet, actual, len(beeman))
    MSE_verlet[idx] = diffVerlet
    diffGear = calc_mse(gear, actual, len(beeman))
    MSE_gear[idx] = diffGear

    # plt.plot(times, beeman, label="beeman", linestyle="solid", color="k")
    # plt.plot(times, verlet, label="verlet", linestyle="dotted", color="r")
    # plt.plot(times, gear, label="gear", linestyle="dashed", color="m")
    # plt.xlabel("Paso temporal (s)")
    # plt.legend()
    # plt.ylabel("Posición de partícula (m)")
    # plt.grid(visible=True)
    # plt.show()

    print("Solución analítica: " + str(actual[-1]) +
          "\nBeeman: " + str(beeman[-1]) +
          "\nVerlet: " + str(verlet[-1]) +
          "\nGear: " + str(gear[-1]))
    print("\nMSE Beeman = " + str(diffBeeman) +
          "\nMSE verlet = " + str(diffVerlet) +
          "\nMSE Gear = " + str(diffGear))


print("0.01\n--------------------\n")
file = open("../output/beeman0.01.txt")
file2 = open("../output/verlet0.01.txt")
file3 = open("../output/gear0.01.txt")
scatter(file, file2, file3, 0.01, 5.01)

idx = idx + 1

print("\n0.001\n--------------------")
file = open("../output/beeman0.001.txt")
file2 = open("../output/verlet0.001.txt")
file3 = open("../output/gear0.001.txt")
scatter(file, file2, file3, 0.001, 5.000)

idx = idx + 1

print("\n0.0001\n--------------------")
file = open("../output/beeman1.0E-4.txt")
file2 = open("../output/verlet1.0E-4.txt")
file3 = open("../output/gear1.0E-4.txt")
scatter(file, file2, file3, 0.0001, 5.0000)

idx = idx + 1

print("\n1E-5\n--------------------")
file = open("../output/beeman1.0E-5.txt")
file2 = open("../output/verlet1.0E-5.txt")
file3 = open("../output/gear1.0E-5.txt")
scatter(file, file2, file3, 0.00001, 5.00001)

print("\nMSE Beeman = " + str(MSE_beeman) +
          "\nMSE verlet = " + str(MSE_verlet) +
          "\nMSE Gear = " + str(MSE_gear))

fig = plt.figure()
ax = fig.add_subplot(2,1,1)
precisions = ["10^-2", "10^-3", "10^-4", "10^-5"]
plt.plot(precisions, MSE_beeman, label="beeman", linestyle="solid", color="k")
plt.plot(precisions, MSE_verlet, label="verlet", linestyle="solid", color="r")
plt.plot(precisions, MSE_gear, label="gear", linestyle="solid", color="m")
ax.set_yscale('log')
plt.xlabel("Paso temporal Δt (s)")
plt.legend()
plt.ylabel("Error Cuadrático Medio")
plt.grid(visible=True)
plt.show()