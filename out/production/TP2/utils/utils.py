def parseOutputData(path):
    file = open(path, 'r')
    l = file.readline()
    outputData = []
    while l:
        data = float(l.strip('\n').replace(',', '.'))
        outputData.append(data)
        l = file.readline()
    file.close()
    return outputData