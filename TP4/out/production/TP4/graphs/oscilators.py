import numpy as np
import matplotlib.pyplot as plt
import math
import pandas as pd
import scipy as sp
from sklearn.neighbors import KernelDensity
from matplotlib import pyplot

A = 1.0
M = 70.0
K = 10000
GAMMA = 100.0
def actualVal(t):
    return A * (np.exp(-(GAMMA / (2 * M)) * t)) * (
            np.cos(np.power((K / M) - (GAMMA * GAMMA / (4 * (M * M))), 0.5) * t))

def calcMSE(calculated, actual, n):
    sum = 0
    for i in range(0, len(calculated)):
        sum = sum + math.pow(calculated[i] - actual[i], 2)
    return sum/n

def parseFile(file):
    readable = file.read().split('\n')[0:]
    i = 0
    for line in readable:
        readable[i] = line.split(' ')[0]
        i = i + 1
    readable.remove('')
    values = np.array(readable).astype(float)
    return values

def scatter():
    file = open("../output/beeman0.01.txt")
    file2 = open("../output/verlet0.01.txt")
    file3 = open("../output/gear0.01.txt")

    values = parseFile(file)
    values2 = parseFile(file2)
    values3 = parseFile(file3)

    actual = np.zeros(len(values))
    times = np.arange(0, 5.01, 0.01)

    for i in range(0, len(values)):
        actual[i] = actualVal(times[i])

    diffBeeman = calcMSE(values, actual, len(values))
    diffVerlet = calcMSE(values2, actual, len(values))
    diffGear = calcMSE(values3, actual, len(values))

    print(str(len(values)) + " " + str(len(actual)))

    print("MSE Beeman = " + str(diffBeeman) + "\nMSE verlet = " + str(diffVerlet) + "\nMSE Gear = " + str(diffGear))
    plt.plot(times, values, label="beeman", linestyle="solid", color="k")
    plt.plot(times, values2, label="verlet", linestyle="dotted", color="r")
    plt.plot(times, values3, label="gear", linestyle="dashed", color="m")
    plt.xlabel("Paso temporal (s)")
    plt.legend()
    plt.ylabel("Posición de partícula (m)")
    plt.grid(visible=True)
    plt.show()

    print("Solución analítica: " + str(actual[-1]) +
          "\nBeeman: " + str(values[-1]) +
          "\nVerlet: " + str(values2[-1]) +
          "\nGear: " + str(values3[-1]))

scatter()