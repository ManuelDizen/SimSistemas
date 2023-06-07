import matplotlib.pyplot as plt
import numpy as np


colors = ['red', 'blue', 'green', 'orange', 'cyan',
          'purple', 'darkgreen', 'darkkhaki', 'coral', 'red']

def deriv(xvalues, yvalues):
    ydiff = np.diff(yvalues)
    xdiff = np.diff(xvalues)
    yprime = ydiff / xdiff
    xprime = []
    for i in range(len(yprime)):
        xtemp = (xvalues[i + 1] + xvalues[i]) / 2
        xprime = np.append(xprime, xtemp)
    return xprime, yprime


def time_to_leave(times, quantities):

    if not quantities:
        return None

    xtimes = []
    i = 1
    while i < len(times):
        diff = quantities[i-1] - quantities[i]
        while diff > 0:
            xtimes += [times[i]]
            diff-=1
        i+=1
    return xtimes

def calculate_average(arrays):
    if not arrays:
        return None

    array_lengths = [len(array) for array in arrays]
    min_length = min(array_lengths)

    average_array = []
    std_array = []
    for i in range(min_length):
        values = []
        for array in arrays:
            values += [array[i]]
        average = np.mean(values)
        std = np.std(values)
        average_array.append(average)
        std_array.append(std)

    return average_array, std_array

def caudal_medio(caudal):
    total = 0
    for i in caudal:
        total += i
    return total/len(caudal)

def getdata(N):
    t_times = []
    t_particles = []
    times_to_leave = []

    for i in range(0, 5):
        file_name = '../../../punto_a_iter_{}_{}.txt'.format(N, i)
        file = open(file_name, 'r')
        lines = file.read().split('\n')
        times = []
        particles = []
        for line in lines:
            data = line.split(" ")
            if data[0] == '' or float(data[0]) > 80.0:
                continue
            times.append(float(data[0]))
            particles.append(float(data[1]))
        ttl = time_to_leave(times, particles)
        t_times.append(times)
        t_particles.append(particles)
        times_to_leave.append(ttl)

    # for i in range(0,len(t_times)):
    #    plt.plot(t_times[i], t_particles[i], color = colors[i])

    avg, tt_std = calculate_average(times_to_leave)
    return avg, tt_std

def descarga(N):

    avg, tt_std = getdata(N)

    yrange = [*range(1, N)]
    yarr = list(yrange)
    yvalues = yarr[::-1]

    plt.plot(avg, yvalues, color='blue')
    #plt.title("N = " + str(N))
    plt.xlabel("Tiempo (s)")
    plt.ylabel("Peatones en recinto")
    plt.errorbar(avg, yvalues, yerr=tt_std, fmt="+", elinewidth=1, capsize=5)
    plt.grid(visible=True)
    plt.show()

def descarga_all():

    Ns = [200, 260, 320, 380]

    graph, lbl = plt.subplots()

    for i in range(len(Ns)):
        avg, tt_std = getdata(Ns[i])

        yrange = [*range(1, Ns[i])]
        yarr = list(yrange)
        yvalues = yarr[::-1]

        plt.plot(avg, yvalues, color=colors[i], label=f'N={Ns[i]}')
        plt.errorbar(avg, yvalues, yerr=tt_std, fmt="+", elinewidth=1, capsize=5)
    plt.xlabel("Tiempo (s)")
    plt.ylabel("Peatones en recinto")
    box = lbl.get_position()
    lbl.set_position([box.x0, box.y0, box.width, box.height * 0.9])
    plt.legend(loc='upper center', bbox_to_anchor=(0.5, 1.2), ncol=4)
    plt.grid(visible=True)
    plt.show()



def calc_window(avg):
    times_mid = []
    caudal_window = []

    init = 5
    window = 5

    while init < max(avg):
        start = init - window
        i = 0
        caudal = 0
        while i < len(avg):
            if avg[i] < init:
                if avg[i] > start:
                    caudal += 1
            else:
                break
            i += 1
        times_mid += [start + (init - start) / 2]
        caudal_window += [caudal / window]
        init += 1

    return times_mid, caudal_window

def caudal_all():
    Ns = [200, 260, 320, 380]

    graph, lbl = plt.subplots()

    for i in range(len(Ns)):
        avg, tt_std = getdata(Ns[i])

        times_mid, caudal_window = calc_window(avg)

        plt.scatter(times_mid, caudal_window, label=f'N={Ns[i]}')
    plt.xlabel("Tiempo (s)")
    plt.ylabel("Caudal")
    box = lbl.get_position()
    lbl.set_position([box.x0, box.y0, box.width, box.height * 0.9])
    plt.legend(loc='upper center', bbox_to_anchor=(0.5, 1.2), ncol=4)
    plt.grid(visible=True)
    plt.show()


def caudal_N(N):

    avg, tt_std = getdata(N)

    times_mid, caudal_window = calc_window(avg)

    plt.scatter(times_mid, caudal_window)
    plt.title("N = " + str(N))
    plt.xlabel("Tiempo (s)")
    plt.ylabel("Caudal")
    plt.grid(visible=True)
    plt.show()
    return np.mean(caudal_window[8:48]), np.std(caudal_window[8:48])

def caudal_stationary(N):
    avg, tt_std = getdata(N)

    times_mid, caudal_window = calc_window(avg)

    return np.mean(caudal_window[8:48]), np.std(caudal_window[8:48])


descarga_all()
caudal_all()

ds = [1.2, 1.8, 2.4, 3.0]
Ns = [200, 260, 320, 380]

caudal_med = []
caudal_std = []

for N in Ns:
    med, std = caudal_stationary(N)
    caudal_med += [med]
    caudal_std += [std]

func = np.polyfit(ds, caudal_med, 1)

print(func[0])

plt.plot(ds, caudal_med)
plt.errorbar(ds, caudal_med, yerr=caudal_std, fmt="o")
plt.xlabel("Tamaño de puerta (m)")
plt.ylabel("Caudal medio")
plt.grid(visible=True)
plt.show()



