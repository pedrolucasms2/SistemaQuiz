import os

def listar_arquivos(pasta):
    for raiz, dirs, arquivos in os.walk(pasta):
        for arquivo in arquivos:
            caminho_completo = os.path.join(raiz, arquivo)
            print(caminho_completo)

if __name__ == "__main__":
    pasta = input("Digite o caminho da pasta: ")
    if os.path.isdir(pasta):
        listar_arquivos(pasta)
    else:
        print("Caminho inválido.")