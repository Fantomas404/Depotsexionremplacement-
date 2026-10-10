package com.opticoncours.seed;

import com.opticoncours.features.auth.Role;
import com.opticoncours.features.auth.User;
import com.opticoncours.features.auth.UserRepository;
import com.opticoncours.features.candidatures.Candidature;
import com.opticoncours.features.candidatures.CandidatureRepository;
import com.opticoncours.features.candidatures.StatutCandidature;
import com.opticoncours.features.concours.Concours;
import com.opticoncours.features.concours.ConcoursRepository;
import com.opticoncours.features.concours.Epreuve;
import com.opticoncours.features.concours.StatutConcours;
import com.opticoncours.features.deliberation.Mention;
import com.opticoncours.features.deliberation.Note;
import com.opticoncours.features.deliberation.NoteRepository;
import com.opticoncours.features.deliberation.Resultat;
import com.opticoncours.features.deliberation.ResultatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

@Component
@RequiredArgsConstructor
@Slf4j
public class DatabaseSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ConcoursRepository concoursRepository;
    private final CandidatureRepository candidatureRepository;
    private final NoteRepository noteRepository;
    private final ResultatRepository resultatRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    @Override
    @Transactional
    public void run(String... args) {
        if (userRepository.count() > 0) {
            log.info("Base de données déjà initialisée.");
            return;
        }

        log.info("Démarrage du peuplement de la base de données OptiConcours...");
        createSampleFiles();

        User admin = User.builder()
                .email("responsable@opticoncours.com")
                .password(passwordEncoder.encode("OptiConcours@2026!"))
                .nom("DIRECTION")
                .prenom("Ressources Humaines")
                .telephone("+33 1 00 00 00 00")
                .role(Role.ROLE_ADMIN)
                .build();
        userRepository.save(admin);

        User cand1 = User.builder()
                .email("sophie.martin@opticoncours.com")
                .password(passwordEncoder.encode("Candidat@2026!"))
                .nom("MARTIN")
                .prenom("Sophie")
                .telephone("+33 6 12 34 56 78")
                .role(Role.ROLE_CANDIDAT)
                .build();
        userRepository.save(cand1);

        User cand2 = User.builder()
                .email("alexandre.dubois@opticoncours.com")
                .password(passwordEncoder.encode("Candidat@2026!"))
                .nom("DUBOIS")
                .prenom("Alexandre")
                .telephone("+33 6 23 45 67 89")
                .role(Role.ROLE_CANDIDAT)
                .build();
        userRepository.save(cand2);

        User cand3 = User.builder()
                .email("julien.moreau@opticoncours.com")
                .password(passwordEncoder.encode("Candidat@2026!"))
                .nom("MOREAU")
                .prenom("Julien")
                .telephone("+33 6 34 56 78 90")
                .role(Role.ROLE_CANDIDAT)
                .build();
        userRepository.save(cand3);

        User cand4 = User.builder()
                .email("lea.bernard@opticoncours.com")
                .password(passwordEncoder.encode("Candidat@2026!"))
                .nom("BERNARD")
                .prenom("Léa")
                .telephone("+33 6 45 67 89 01")
                .role(Role.ROLE_CANDIDAT)
                .build();
        userRepository.save(cand4);

        User cand5 = User.builder()
                .email("claire.petit@opticoncours.com")
                .password(passwordEncoder.encode("Candidat@2026!"))
                .nom("PETIT")
                .prenom("Claire")
                .telephone("+33 6 56 78 90 12")
                .role(Role.ROLE_CANDIDAT)
                .build();
        userRepository.save(cand5);

        Concours c1 = Concours.builder()
                .titre("Concours National d'Accès aux Cadres Supérieurs de l'Administration Publique (Session 2026)")
                .description("Sélection rigoureuse pour l'intégration des futurs cadres de la fonction publique d'État.")
                .conditions("Être titulaire d'un diplôme de Master 2 ou diplôme d'ingénieur reconnu.")
                .dateDebut(LocalDate.now().minusMonths(2))
                .dateFin(LocalDate.now().minusDays(10))
                .dateDeliberation(LocalDate.now().minusDays(1))
                .placesDisponibles(2)
                .statut(StatutConcours.PUBLIE)
                .build();

        Epreuve e1c1 = Epreuve.builder().intitule("Épreuve de Culture Générale & Enjeux Contemporains").coefficient(2.0).dureeMinutes(180).build();
        Epreuve e2c1 = Epreuve.builder().intitule("Droit Public & Institutions Administratives").coefficient(3.0).dureeMinutes(240).build();
        Epreuve e3c1 = Epreuve.builder().intitule("Économie & Finances Publiques").coefficient(3.0).dureeMinutes(240).build();
        c1.addEpreuve(e1c1);
        c1.addEpreuve(e2c1);
        c1.addEpreuve(e3c1);
        c1 = concoursRepository.save(c1);

        Candidature d1c1 = createCandidature(cand1, c1, StatutCandidature.DELIBERE);
        Candidature d2c1 = createCandidature(cand2, c1, StatutCandidature.DELIBERE);
        Candidature d3c1 = createCandidature(cand3, c1, StatutCandidature.DELIBERE);

        saveNote(d1c1, e1c1, 16.5);
        saveNote(d1c1, e2c1, 15.0);
        saveNote(d1c1, e3c1, 17.0);

        saveNote(d2c1, e1c1, 14.0);
        saveNote(d2c1, e2c1, 13.5);
        saveNote(d2c1, e3c1, 14.5);

        saveNote(d3c1, e1c1, 11.0);
        saveNote(d3c1, e2c1, 9.5);
        saveNote(d3c1, e3c1, 12.0);

        saveResultat(d1c1, 16.12, 1, Mention.ADMIS, true);
        saveResultat(d2c1, 14.00, 2, Mention.ADMIS, true);
        saveResultat(d3c1, 10.81, 3, Mention.LISTE_D_ATTENTE, true);

        Concours c2 = Concours.builder()
                .titre("Concours d'Ingénieurs d'État en Systèmes Numériques & Cybersécurité")
                .description("Recrutement spécialisé pour les directions informatiques de l'État.")
                .conditions("Bac+5 en informatique, génie logiciel ou cybersécurité exigé.")
                .dateDebut(LocalDate.now().minusDays(15))
                .dateFin(LocalDate.now().plusMonths(1))
                .dateDeliberation(LocalDate.now().plusMonths(2))
                .placesDisponibles(10)
                .statut(StatutConcours.OUVERT)
                .build();
        
        Epreuve e1c2 = Epreuve.builder().intitule("Architecture Logicielle & Algorithmique Avancée").coefficient(4.0).dureeMinutes(240).build();
        Epreuve e2c2 = Epreuve.builder().intitule("Ingénierie des Données & Cloud Computing").coefficient(3.0).dureeMinutes(180).build();
        c2.addEpreuve(e1c2);
        c2.addEpreuve(e2c2);
        c2 = concoursRepository.save(c2);

        createCandidature(cand4, c2, StatutCandidature.DOSSIER_COMPLET);
        createCandidature(cand5, c2, StatutCandidature.EN_ATTENTE);

        log.info("Base de données OptiConcours initialisée avec succès !");
        log.info("Compte Administrateur : responsable@opticoncours.com / OptiConcours@2026!");
        log.info("Comptes Candidats : (ex: sophie.martin@opticoncours.com / Candidat@2026!)");
    }

    private Candidature createCandidature(User cand, Concours concours, StatutCandidature statut) {
        Candidature c = Candidature.builder()
                .candidat(cand)
                .concours(concours)
                .cvUrl("/api/files/sample_cv.pdf")
                .diplomeUrl("/api/files/sample_diplome.pdf")
                .photoUrl("/api/files/sample_photo.jpg")
                .statut(statut)
                .dateSoumission(LocalDateTime.now().minusDays(5))
                .build();
        return candidatureRepository.save(c);
    }

    private void saveNote(Candidature c, Epreuve e, double val) {
        Note n = Note.builder()
                .candidature(c)
                .epreuve(e)
                .valeurNote(val)
                .build();
        noteRepository.save(n);
    }

    private void saveResultat(Candidature c, double moy, int rang, Mention mention, boolean publie) {
        Resultat r = Resultat.builder()
                .candidature(c)
                .moyenneGenerale(moy)
                .rang(rang)
                .mention(mention)
                .estPublie(publie)
                .datePublication(publie ? LocalDateTime.now().minusDays(1) : null)
                .build();
        resultatRepository.save(r);
    }

    private void createSampleFiles() {
        try {
            Path dir = Paths.get(uploadDir).toAbsolutePath().normalize();
            Files.createDirectories(dir);

            Path cvPath = dir.resolve("sample_cv.pdf");
            Path diplomePath = dir.resolve("sample_diplome.pdf");
            Path photoPath = dir.resolve("sample_photo.jpg");

            byte[] cvPdf = createValidPdf(
                    "CURRICULUM VITAE",
                    "Candidature Officielle - OptiConcours",
                    "Profil type de candidat administratif"
            );
            Files.write(cvPath, cvPdf);

            byte[] diplomePdf = createValidPdf(
                    "DIPLOME CERTIFIE",
                    "Validation des Acquis Academiques",
                    "Document de demonstration genere par OptiConcours."
            );
            Files.write(diplomePath, diplomePdf);

            byte[] photoJpg = createValidPhoto("OptiConcours");
            Files.write(photoPath, photoJpg);
        } catch (Exception ex) {
            log.warn("Impossible de créer les fichiers de démonstration: {}", ex.getMessage());
        }
    }

    private byte[] createValidPdf(String title, String subtitle, String body) {
        StringBuilder sb = new StringBuilder();
        sb.append("BT\n/F1 18 Tf\n50 730 Td\n(").append(title).append(") Tj\n");
        sb.append("/F1 12 Tf\n0 -25 Td\n(").append(subtitle).append(") Tj\n");
        sb.append("/F1 10 Tf\n0 -20 Td\n(--------------------------------------------------------------------------------) Tj\n");
        sb.append("0 -18 Td\n(").append(body).append(") Tj\nET\n");

        byte[] streamData = sb.toString().getBytes(StandardCharsets.ISO_8859_1);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            List<Integer> offsets = new ArrayList<>();
            baos.write("%PDF-1.4\n".getBytes(StandardCharsets.ISO_8859_1));

            offsets.add(baos.size());
            baos.write("1 0 obj\n<< /Type /Catalog /Pages 2 0 R >>\nendobj\n".getBytes(StandardCharsets.ISO_8859_1));

            offsets.add(baos.size());
            baos.write("2 0 obj\n<< /Type /Pages /Kids [3 0 R] /Count 1 >>\nendobj\n".getBytes(StandardCharsets.ISO_8859_1));

            offsets.add(baos.size());
            baos.write("3 0 obj\n<< /Type /Page /Parent 2 0 R /MediaBox [0 0 612 792] /Contents 4 0 R /Resources << /Font << /F1 5 0 R >> >> >>\nendobj\n".getBytes(StandardCharsets.ISO_8859_1));

            offsets.add(baos.size());
            baos.write(("4 0 obj\n<< /Length " + streamData.length + " >>\nstream\n").getBytes(StandardCharsets.ISO_8859_1));
            baos.write(streamData);
            baos.write("\nendstream\nendobj\n".getBytes(StandardCharsets.ISO_8859_1));

            offsets.add(baos.size());
            baos.write("5 0 obj\n<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica >>\nendobj\n".getBytes(StandardCharsets.ISO_8859_1));

            int startxref = baos.size();
            baos.write("xref\n0 6\n0000000000 65535 f \n".getBytes(StandardCharsets.ISO_8859_1));
            for (int offset : offsets) {
                baos.write(String.format("%010d 00000 n \n", offset).getBytes(StandardCharsets.ISO_8859_1));
            }

            baos.write(("trailer\n<< /Size 6 /Root 1 0 R >>\nstartxref\n" + startxref + "\n%%EOF\n").getBytes(StandardCharsets.ISO_8859_1));
        } catch (IOException ignored) {}

        return baos.toByteArray();
    }

    private byte[] createValidPhoto(String name) {
        BufferedImage img = new BufferedImage(300, 300, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g.setColor(new Color(30, 58, 138)); // bg-blue-900
        g.fillRect(0, 0, 300, 300);

        g.setColor(new Color(241, 245, 249)); // bg-slate-100
        g.fillOval(75, 35, 150, 150);

        g.setColor(new Color(71, 85, 105)); // slate-600
        g.fillOval(110, 60, 80, 80);
        g.fillArc(85, 130, 130, 100, 0, 180);

        g.setColor(Color.WHITE);
        g.setFont(new Font("SansSerif", Font.BOLD, 16));
        FontMetrics fm = g.getFontMetrics();
        int textWidth = fm.stringWidth(name);
        g.drawString(name, (300 - textWidth) / 2, 235);

        g.dispose();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ImageIO.write(img, "jpg", baos);
        } catch (IOException ignored) {}
        return baos.toByteArray();
    }
}
