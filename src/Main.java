import java.util.*;

// Müşteri sınıfı
class Musteri {
    int musteriID;
    String isim;
    String soyisim;
    LinkedList<Gonderi> gonderiGecmisi;

    public Musteri(int musteriID, String isim, String soyisim) {
        this.musteriID = musteriID;
        this.isim = isim;
        this.soyisim = soyisim;
        this.gonderiGecmisi = new LinkedList<>();
    }

    // Gönderi ekleme (tarih sırasına göre ekler)
    public void gonderiEkle(Gonderi yeniGonderi) {
        int i = 0;
        while (i < gonderiGecmisi.size() && gonderiGecmisi.get(i).tarih.before(yeniGonderi.tarih)) {
            i++;
        }
        gonderiGecmisi.add(i, yeniGonderi);
    }

    // Gönderim geçmişini listeleme
    public void gonderiGecmisiniListele() {
        if (gonderiGecmisi.isEmpty()) {
            System.out.println("Gönderi geçmişi boş.");
        } else {
            for (Gonderi gonderi : gonderiGecmisi) {
                System.out.println("Gönderi ID: " + gonderi.gonderiID + ", Tarih: " + gonderi.tarih + ", Durum: " + gonderi.teslimDurumu + ", Süre: " + gonderi.teslimSuresi + " gün");
            }
        }
    }
}

// Gönderi sınıfı
class Gonderi {
    int gonderiID;
    Date tarih;
    String teslimDurumu;
    int teslimSuresi;

    public Gonderi(int gonderiID, Date tarih, String teslimDurumu, int teslimSuresi) {
        this.gonderiID = gonderiID;
        this.tarih = tarih;
        this.teslimDurumu = teslimDurumu;
        this.teslimSuresi = teslimSuresi;
    }
}

// Öncelikli Kargo sınıfı (Priority Queue kullanımı için)
class OncelikliKargo implements Comparable<OncelikliKargo> {
    int gonderiID;
    int teslimSuresi;
    String kargoDurumu;

    public OncelikliKargo(int gonderiID, int teslimSuresi, String kargoDurumu) {
        this.gonderiID = gonderiID;
        this.teslimSuresi = teslimSuresi;
        this.kargoDurumu = kargoDurumu;
    }

    @Override
    public int compareTo(OncelikliKargo o) {
        return Integer.compare(this.teslimSuresi, o.teslimSuresi);
    }
}

// Teslimat rotaları için düğüm sınıfı (Tree yapısı için)
class Sehir {
    String sehirAdi;
    int sehirID;
    List<Sehir> altSehirler;
    int teslimSuresi;

    public Sehir(String sehirAdi, int sehirID, int teslimSuresi) {
        this.sehirAdi = sehirAdi;
        this.sehirID = sehirID;
        this.altSehirler = new ArrayList<>();
        this.teslimSuresi = teslimSuresi;
    }

    public void altSehirEkle(Sehir altSehir) {
        altSehirler.add(altSehir);
    }

    public void agaciGoruntule(String prefix) {
        System.out.println(prefix + "- " + sehirAdi + " (Teslim Süre: " + teslimSuresi + " gün)");
        for (Sehir altSehir : altSehirler) {
            altSehir.agaciGoruntule(prefix + "    ");
        }
    }

    public int enKisaTeslimatSuresi() {
        int minSure = this.teslimSuresi;
        for (Sehir altSehir : altSehirler) {
            minSure = Math.min(minSure, altSehir.enKisaTeslimatSuresi());
        }
        return minSure;
    }
}

// Ana sınıf
class KargoTakipSistemi {
    List<Musteri> musteriler;
    PriorityQueue<OncelikliKargo> oncelikliKargolar;
    Stack<Gonderi> gonderimGecmisi;
    Sehir merkez;
    Scanner scanner;

    public KargoTakipSistemi() {
        this.musteriler = new ArrayList<>();
        this.oncelikliKargolar = new PriorityQueue<>();
        this.gonderimGecmisi = new Stack<>();
        this.merkez = new Sehir("Merkez", 0, 5);
        this.scanner = new Scanner(System.in);
    }

    public void musteriEkle() {
        System.out.print("Müşteri ID: ");
        int musteriID = scanner.nextInt();
        scanner.nextLine();
        System.out.print("İsim: ");
        String isim = scanner.nextLine();
        System.out.print("Soyisim: ");
        String soyisim = scanner.nextLine();
        musteriler.add(new Musteri(musteriID, isim, soyisim));
        System.out.println("Yeni müşteri eklendi.");
    }

    public void gonderiEkle() {
        System.out.print("Müşteri ID: ");
        int musteriID = scanner.nextInt();
        Musteri musteri = musteriler.stream().filter(m -> m.musteriID == musteriID).findFirst().orElse(null);
        if (musteri == null) {
            System.out.println("Müşteri bulunamadı.");
            return;
        }
        System.out.print("Gönderi ID: ");
        int gonderiID = scanner.nextInt();
        System.out.print("Teslim Süre (gün): ");
        int teslimSuresi = scanner.nextInt();
        scanner.nextLine();
        System.out.print("Teslim Durumu (Teslim Edildi/Teslim Edilmedi): ");
        String teslimDurumu = scanner.nextLine();
        Gonderi yeniGonderi = new Gonderi(gonderiID, new Date(), teslimDurumu, teslimSuresi);
        musteri.gonderiEkle(yeniGonderi);
        gonderimGecmisi.push(yeniGonderi);
        System.out.println("Gönderi başarıyla eklendi.");
    }

    public void oncelikliKargoEkle() {
        System.out.print("Gönderi ID: ");
        int gonderiID = scanner.nextInt();
        System.out.print("Teslim Süre (gün): ");
        int teslimSuresi = scanner.nextInt();
        scanner.nextLine();
        System.out.print("Kargo Durumu (Teslimatta/Teslim Edildi): ");
        String kargoDurumu = scanner.nextLine();
        oncelikliKargolar.add(new OncelikliKargo(gonderiID, teslimSuresi, kargoDurumu));
        System.out.println("Kargo öncelikli sıraya eklendi.");
    }

    public void teslimatRotalariniGoruntule() {
        System.out.println("Teslimat Rotaları:");
        merkez.agaciGoruntule("");
    }

    public void sehirEkle() {
        System.out.print("Şehir Adı: ");
        String sehirAdi = scanner.nextLine();
        System.out.print("Şehir ID: ");
        int sehirID = scanner.nextInt();
        System.out.print("Teslim Süre (gün): ");
        int teslimSuresi = scanner.nextInt();
        scanner.nextLine();
        Sehir yeniSehir = new Sehir(sehirAdi, sehirID, teslimSuresi);
        merkez.altSehirEkle(yeniSehir);
        System.out.println("Şehir başarıyla eklendi.");
    }

    public void sonBesGonderiyiGoruntule() {
        if (gonderimGecmisi.isEmpty()) {
            System.out.println("Gönderim geçmişi boş.");
            return;
        }
        System.out.println("Son 5 Gönderi:");
        List<Gonderi> gecmisKopya = new ArrayList<>(gonderimGecmisi);
        for (int i = gecmisKopya.size() - 1; i >= Math.max(0, gecmisKopya.size() - 5); i--) {
            Gonderi gonderi = gecmisKopya.get(i);
            System.out.println("Gönderi ID: " + gonderi.gonderiID + ", Tarih: " + gonderi.tarih + ", Durum: " + gonderi.teslimDurumu);
        }
    }

    public void kargoDurumuSorgula() {
        System.out.print("Teslim Edildi mi? (Evet/Hayır): ");
        String durum = scanner.nextLine();
        boolean teslimEdildi = durum.equalsIgnoreCase("Evet");

        List<Gonderi> tumGonderiler = new ArrayList<>();
        for (Musteri musteri : musteriler) {
            tumGonderiler.addAll(musteri.gonderiGecmisi);
        }

        if (teslimEdildi) {
            tumGonderiler.sort(Comparator.comparingInt(g -> g.gonderiID));
            System.out.println("Teslim Edilmiş Kargolar (ID sırasına göre):");
            for (Gonderi g : tumGonderiler) {
                if (g.teslimDurumu.equalsIgnoreCase("Teslim Edildi")) {
                    System.out.println("Gönderi ID: " + g.gonderiID);
                }
            }
        } else {
            tumGonderiler.sort(Comparator.comparingInt(g -> g.teslimSuresi));
            System.out.println("Teslim Edilmemiş Kargolar (Teslim Süresi sırasına göre):");
            for (Gonderi g : tumGonderiler) {
                if (g.teslimDurumu.equalsIgnoreCase("Teslim Edilmedi")) {
                    System.out.println("Gönderi ID: " + g.gonderiID + ", Teslim Süresi: " + g.teslimSuresi + " gün");
                }
            }
        }
    }
    // Teslimat rotalarını kullanıcıdan almak ve ağaç yapısında görselleştirmek için güncellenmiş sınıf
    public void sehirRotalariniOlustur() {
        System.out.print("Kaç adet şehir eklemek istiyorsunuz? ");
        int sehirSayisi = scanner.nextInt();
        scanner.nextLine();

        Map<Integer, Sehir> sehirHaritasi = new HashMap<>();
        sehirHaritasi.put(merkez.sehirID, merkez);

        for (int i = 0; i < sehirSayisi; i++) {
            System.out.print("Şehir Adı: ");
            String sehirAdi = scanner.nextLine();
            System.out.print("Şehir ID: ");
            int sehirID = scanner.nextInt();
            System.out.print("Teslim Süresi (gün): ");
            int teslimSuresi = scanner.nextInt();
            scanner.nextLine();

            Sehir yeniSehir = new Sehir(sehirAdi, sehirID, teslimSuresi);
            sehirHaritasi.put(sehirID, yeniSehir);

            System.out.print(sehirAdi + " hangi şehirle bağlantılı (Şehir ID giriniz): ");
            int ustSehirID = scanner.nextInt();
            scanner.nextLine();

            Sehir ustSehir = sehirHaritasi.getOrDefault(ustSehirID, null);
            if (ustSehir != null) {
                ustSehir.altSehirEkle(yeniSehir);
            } else {
                System.out.println("Geçersiz üst şehir ID! Şehir merkezine bağlanıyor.");
                merkez.altSehirEkle(yeniSehir);
            }
        }

        System.out.println("Tüm şehirler ve rotalar başarıyla eklendi!");
    }

    public void menu() {
        while (true) {
            System.out.println("\nKargo Takip Sistemi Menü:");
            System.out.println("1. Yeni müşteri ekle.");
            System.out.println("2. Kargo gönderimi ekle.");
            System.out.println("3. Kargo durumu sorgula.");
            System.out.println("4. Gönderim geçmişini görüntüle.");
            System.out.println("5. Son 5 gönderiyi görüntüle."); // Yeni Menü Seçeneği
            System.out.println("6. Teslimat rotalarını göster.");
            System.out.println("7. Teslimat rotalarını oluştur.");
            System.out.println("8. Çıkış.");
            System.out.print("Seçiminizi yapınız: ");

            int secim = scanner.nextInt();
            scanner.nextLine(); // Giriş sonrası tamponu temizle.

            switch (secim) {
                case 1 -> musteriEkle();
                case 2 -> gonderiEkle();
                case 3 -> kargoDurumuSorgula();
                case 4 -> {
                    System.out.print("Müşteri ID: ");
                    int musteriID = scanner.nextInt();
                    Musteri musteri = musteriler.stream()
                            .filter(m -> m.musteriID == musteriID)
                            .findFirst()
                            .orElse(null);
                    if (musteri == null) {
                        System.out.println("Müşteri bulunamadı.");
                    } else {
                        musteri.gonderiGecmisiniListele();
                    }
                }
                case 5 -> sonBesGonderiyiGoruntule(); // Yeni Menü İşlevi Bağlantısı
                case 6 -> teslimatRotalariniGoruntule();
                case 7 -> sehirRotalariniOlustur();
                case 8 -> {
                    System.out.println("Sistemden çıkılıyor...");
                    return;
                }
                default -> System.out.println("Geçersiz seçim. Lütfen tekrar deneyin.");
            }
        }
    }

    public static void main(String[] args) {
        KargoTakipSistemi sistem = new KargoTakipSistemi();
        sistem.menu();
    }
}
