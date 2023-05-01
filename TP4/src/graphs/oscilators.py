import numpy as np
import matplotlib.pyplot as plt
import math
import pandas as pd
import scipy as sp
from sklearn.neighbors import KernelDensity
from matplotlib import pyplot

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

    # readable = file.read().split('\n')[0:]
    # print(str(len(readable)))
    # i = 0
    # for line in readable:
    #     readable[i] = line.split(' ')[0]
    #     i = i +1
    # readable.remove('')
    # values = np.array(readable).astype(float)
    # print(values)
    values = parseFile(file)
    values2 = parseFile(file2)
    values3 = parseFile(file3)

    times = np.arange(0, 5.01, 0.01)

    plt.plot(times, values, label="beeman")
    plt.plot(times, values2, label="verlet")
    plt.plot(times, values3, label="gear", linestyle="dashed", color="m")
    plt.xlabel("Paso temporal (s)")
    plt.legend()
    plt.ylabel("Posición de partícula (m)")
    plt.grid(visible=True)
    plt.show()

scatter()