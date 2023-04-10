from utils import *
import numpy as np
import matplotlib.pyplot as plt

etas = [0.5, 1.0, 2.0, 3.0, 4.0]

graph, lbl= plt.subplots()

for i in etas:
    x_axis_values = range(999)
    va = parseOutputData('va_output_N1000_L10_eta=' + str(i) + '.txt')
    y_axis_values = va[1:]
    print(y_axis_values)
    plt.plot(x_axis_values, y_axis_values, label=f'η = {i}')
plt.xlabel("Iteraciones")
plt.ylabel("Va")
box = lbl.get_position()
lbl.set_position([box.x0, box.y0, box.width, box.height * 0.9])
plt.legend(loc='upper center', bbox_to_anchor=(0.5, 1.2), ncol=4)
plt.show()

graph, lbl= plt.subplots()

ns = [50, 200, 2000]

for i in ns:
    x_axis_values = range(999)
    va = parseOutputData('va_output_N' + str(i) + '_L10_eta=1.0.txt')
    y_axis_values = va[1:]
    print(y_axis_values)
    plt.plot(x_axis_values, y_axis_values, label=f'ρ = {i}')
plt.xlabel("Iteraciones")
plt.ylabel("Va")
box = lbl.get_position()
lbl.set_position([box.x0, box.y0, box.width, box.height * 0.9])
plt.legend(loc='upper center', bbox_to_anchor=(0.5, 1.2), ncol=4)
plt.show()