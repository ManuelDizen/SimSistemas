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
    for i in range(min_length):
        sum_values = sum(array[i] for array in arrays)
        average = sum_values / len(arrays)
        average_array.append(average)

    return average_array

def caudal_medio(caudal):
    total = 0
    for i in caudal:
        total += i
    return total/len(caudal)

# def caudal_graph(N, d):
#     t_times = []
#     t_particles = []
#     times_to_leave = []
#
#     for i in range(0, 5):
#         file_name = '../../../punto_a_iter_{}_{}.txt'.format(N, i)
#         file = open(file_name, 'r')
#         lines = file.read().split('\n')
#         times = []
#         particles = []
#         for line in lines:
#             data = line.split(" ")
#             if data[0] == '' or float(data[0]) > 80.0:
#                 continue
#             times.append(float(data[0]))
#             particles.append(float(data[1]))
#         ttl = time_to_leave(times, particles)
#         print(len(ttl))
#         t_times.append(times)
#         t_particles.append(particles)
#         times_to_leave.append(ttl)

    # fig = plt.figure()
    # # for i in range(0,len(t_times)):
    # #    plt.plot(t_times[i], t_particles[i], color = colors[i])
    # array = (len(array) for array in t_times)
    # print(array)
    #
    # avg = calculate_average(times_to_leave)
    # yvalues = range(1, N)
    # plt.plot(avg, yvalues, color='blue')
    # plt.title("N = " + str(N))
    # plt.xlabel("Tiempo (s)")
    # plt.ylabel("Peatones evacuados")
    # plt.grid(visible=True)
    # plt.show()
    #
    # xprime, yprime = deriv(avg, yvalues)
    #
    # plt.plot(xprime, yprime, color='red')
    # plt.title("N = " + str(N))
    # plt.xlabel("Tiempo (s)")
    # plt.ylabel("Caudal")
    # plt.grid(visible=True)
    # plt.show()
    # return np.mean(yprime[5:65]), np.std(yprime[5:65])/(pow(60, 0.5))

def caudal_N(N):
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

    fig = plt.figure()
    # for i in range(0,len(t_times)):
    #    plt.plot(t_times[i], t_particles[i], color = colors[i])

    avg = calculate_average(times_to_leave)
    yvalues = range(1, N)
    plt.plot(avg, yvalues, color='blue')
    plt.title("N = " + str(N))
    plt.xlabel("Tiempo (s)")
    plt.ylabel("Peatones evacuados")
    plt.grid(visible=True)
    plt.show()

    # avg_particles = calculate_average(t_particles)
    #
    # array_lengths = [len(t_t) for t_t in t_times]
    # min_length = min(array_lengths)
    #
    # time_arr = t_times[0][:min_length]
    #
    # window = 200
    # init = 200
    #
    # caudal_window = []
    # times_mid = []
    #
    # print(min_length)
    #
    # while(init < min_length):
    #     t2 = init-window
    #     caudal = avg_particles[t2] - avg_particles[init]
    #     t_m = time_arr[t2] + (time_arr[init] - time_arr[t2])/2
    #     times_mid += [t_m]
    #     caudal_window += [caudal/5]
    #     init += 40

    times_mid = []
    caudal_window = []

    init = 5
    window = 5

    print(max(avg))


    while init < max(avg):
        start = init-window
        i = 0
        caudal = 0
        while i < len(avg):
            if avg[i] < init:
                if avg[i] > start:
                    caudal += 1
            else:
                break
            i += 1
        times_mid += [start + (init-start)/2]
        caudal_window += [caudal/window]
        init += 1

    print(caudal_window)
    plt.scatter(times_mid, caudal_window)
    plt.title("N = " + str(N))
    plt.xlabel("Tiempo (s)")
    plt.ylabel("Caudal")
    plt.grid(visible=True)
    plt.show()
    return np.mean(caudal_window[8:48]), np.std(caudal_window[8:48])

ds = [1.2, 1.8, 2.4, 3.0]
Ns = [200, 260, 320, 380]

caudal_med = []
caudal_std = []

for N in Ns:
    med, std = caudal_N(N)
    caudal_med += [med]
    caudal_std += [std]

print(caudal_med)
print(caudal_std)

plt.plot(ds, caudal_med)
plt.errorbar(ds, caudal_med, yerr=caudal_std, fmt="o")
plt.xlabel("Tamaño de puerta (m)")
plt.ylabel("Caudal medio")
plt.grid(visible=True)
plt.show()



