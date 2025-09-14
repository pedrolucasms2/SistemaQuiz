import os
import re

# Regex para capturar emojis (faixa Unicode comum)
emoji_pattern = re.compile(
    "["
    "\U0001F600-\U0001F64F"  # emoticons
    "\U0001F300-\U0001F5FF"  # símbolos e pictogramas
    "\U0001F680-\U0001F6FF"  # transporte e mapas
    "\U0001F1E0-\U0001F1FF"  # bandeiras
    "\U00002700-\U000027BF"  # vários símbolos
    "\U0001F900-\U0001F9FF"  # símbolos adicionais
    "\U0001FA70-\U0001FAFF"  # símbolos recentes
    "]+",
    flags=re.UNICODE
)

def clean_file(path):
    try:
        with open(path, "r", encoding="utf-8", errors="ignore") as f:
            content = f.read()
        cleaned = emoji_pattern.sub("", content)
        with open(path, "w", encoding="utf-8") as f:
            f.write(cleaned)
        print(f"✔ Limpo: {path}")
    except Exception as e:
        print(f"⚠ Erro ao processar {path}: {e}")

def main():
    pasta = input("Digite o caminho da pasta que deseja limpar: ").strip()

    if not os.path.isdir(pasta):
        print("❌ Caminho inválido!")
        return

    for root, _, files in os.walk(pasta):
        for file in files:
            if file.endswith(".java"):  # só arquivos .kava
                file_path = os.path.join(root, file)
                clean_file(file_path)

if __name__ == "__main__":
    main()