package com.example.appmussicapi.repository

import android.util.Log
import com.example.appmussicapi.data.api.CloudinaryApiClient
import com.example.appmussicapi.data.model.Song

class SongRepository {
    suspend fun getSongs(): List<Song> {
        Log.d("SongRepository", "Loading songs from Cloudinary")
        
        return listOf(
            Song(
                id = "nhac_mp3/gia_nhu_anh_lang_im",
                name = "Giá Như Anh Lặng Im",
                artist = "Soobin",
                url = "https://res.cloudinary.com/dyugji8gz/video/upload/v1753159112/GI%C3%81_NH%C6%AF_ANH_L%E1%BA%B6NG_IM_-_OnlyC_ft._Lou_Ho%C3%A0ng_ft._Quang_H%C3%B9ng_-_Acoustic_Cover_HD_zslefe.mp3"
            ),
            Song(
                id = "nhac_mp3/thu_cuoi",
                name = "Thu Cuối",
                artist = "MrT, Yanbi",
                url = "https://res.cloudinary.com/dyugji8gz/video/upload/v1753159112/Mr.T_-_Thu_Cu%E1%BB%91i_ft_Yanbi_H%E1%BA%B1ng_BingBoong_Official_MV_qb8k6g.mp3"
            ),
            Song(
                id = "nhac_mp3/toi_cho_co_gai_do",
                name = "Tội cho cô gái đó",
                artist = "Khắc Việt",
                url = "https://res.cloudinary.com/dyugji8gz/video/upload/v1753159112/Mr.T_-_Thu_Cu%E1%BB%91i_ft_Yanbi_H%E1%BA%B1ng_BingBoong_Official_MV_qb8k6g.mp3"
            ),
            Song(
                id = "nhac_mp3/yeu_mot_nguoi_co_le",
                name = "Yêu Một Người Có Lẽ - Lou Hoang, Only C",
                url = "https://res.cloudinary.com/dyugji8gz/video/upload/v1753159111/Y%C3%AAu_M%E1%BB%99t_Ng%C6%B0%E1%BB%9Di_C%C3%B3_L%E1%BA%BD_-_Lou_Ho%C3%A0ng_-_Miu_L%C3%AA_-_Miu_L%C3%AA_Official_cuckbl.mp3"
            ),
            Song(
                id = "nhac_mp3/nguoi_ay",
                name = "Người Ấy - Trịnh Thằng Bình",
                url = "https://res.cloudinary.com/dyugji8gz/video/upload/v1753159110/Ng%C6%B0%E1%BB%9Di_%E1%BA%A4y_-_Tr%E1%BB%8Bnh_Th%C4%83ng_B%C3%ACnh_Lyrics_rit7l3.mp3"
            ),
            Song(
                id = "nhac_mp3/yeu_lai_tu_dau",
                name = "Yêu lại từ đầu - Khắc Việt",
                url = "https://res.cloudinary.com/dyugji8gz/video/upload/v1753159110/KH%E1%BA%AEC_VI%E1%BB%86T_-_Y%C3%AAu_L%E1%BA%A1i_T%E1%BB%AB_%C4%90%E1%BA%A7u_Official_flotnc.mp3"
            ),
            Song(
                id = "nhac_mp3/con_mua_ngang_qua_2",
                name = "Cơn Mưa Ngang Qua 2-Sơn Tùng",
                url = "https://res.cloudinary.com/dyugji8gz/video/upload/v1753159110/C%C6%A1n_M%C6%B0a_Ngang_Qua_Part_2_-_M-TP_Video_Lyric_Kara_hrur0v.mp3"
            ),
            Song(
                id = "nhac_mp3/con_mua_ngang_qua_3",
                name = "Cơn Mưa Ngang Qua 3-Sơn Tùng",
                url = "https://res.cloudinary.com/dyugji8gz/video/upload/v1753159110/C%C6%A1n_M%C6%B0a_Ngang_Qua_Part_3_-_M-TP_Video_Lyric_Kara_yjkze9.mp3"
            ),
            Song(
                id = "nhac_mp3/dem_ngay_xa_em",
                name = "Đếm ngày xa em- Lou Hoàng, Only C",
                url = "https://res.cloudinary.com/dyugji8gz/video/upload/v1753159110/%C4%90%E1%BA%BFm_Ng%C3%A0y_Xa_Em_-_Only_C_ft._Lou_Ho%C3%A0ng_-_Official_MV_-_Nh%E1%BA%A1c_tr%E1%BA%BB_m%E1%BB%9Bi_hay_tuy%E1%BB%83n_ch%E1%BB%8Dn_uxuyyo.mp3"
            ),
            Song(
                id = "nhac_mp3/vo_tan",
                name = "Vỡ Tan - Trịnh Thằng Bình",
                url = "https://res.cloudinary.com/dyugji8gz/video/upload/v1753159109/V%E1%BB%A1_Tan_-_Tr%E1%BB%8Bnh_Th%C4%83ng_B%C3%ACnh_tgnjl8.mp3"
            ),
            Song(
                id = "nhac_mp3/con_mua_ngang_qua",
                name = "Cơn Mưa Ngang Qua - Sơn Tùng",
                url = "https://res.cloudinary.com/dyugji8gz/video/upload/v1753159108/C%C6%A1n_M%C6%B0a_Ngang_Qua_yl1n5a.mp3"
            ),
            Song(
                id = "nhac_mp3/ve_ben_anh",
                name = "Về Bênh Anh - Jack 3,5",
                url = "https://res.cloudinary.com/dyugji8gz/video/upload/v1753159107/OFFICIAL_MV_V%E1%BB%80_B%C3%8AN_ANH_-_Jack_G5R_yewrjg.mp3"
            ),
            Song(
                id = "nhac_mp3/tuy_am",
                name = "Túy Âm",
                artist = "Masew",
                url = "https://res.cloudinary.com/dyugji8gz/video/upload/v1753159098/T%C3%BAy_%C3%82m_-_Xesi_x_Masew_x_Nhatnguyen_yifyjl.mp3"
            )
        ).also {
            Log.d("SongRepository", "Loaded ${it.size} songs from Cloudinary")
        }
    }

    private fun getFallbackSongs(): List<Song> {
        return listOf(
            Song(
                id = "nhac_mp3/song1",
                name = "In My Head feat. The Chainsmokers",
                url = "https://res.cloudinary.com/dyugji8gz/video/upload/v1753156181/In_My_Head_feat._The_Chainsmokers_y3fxey.m4a"
            ),
            Song(
                id = "nhac_mp3/song2", 
                name = "Túy Âm - Xesi x Masew x Nhatnguyen",
                url = "https://res.cloudinary.com/dyugji8gz/video/upload/v1753159098/T%C3%BAy_%C3%82m_-_Xesi_x_Masew_x_Nhatnguyen_yifyjl.mp3"
            ),
            Song(
                id = "nhac_mp3/song3",
                name = "VỀ BÊN ANH - Jack G5R", 
                url = "https://res.cloudinary.com/dyugji8gz/video/upload/v1753159107/OFFICIAL_MV_V%E1%BB%80_B%C3%8AN_ANH_-_Jack_G5R_yewrjg.mp3"
            )
        )
    }
}
